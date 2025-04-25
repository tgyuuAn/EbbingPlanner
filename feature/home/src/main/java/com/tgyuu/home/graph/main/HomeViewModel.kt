package com.tgyuu.home.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.EditTodoRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler,
    internal val eventBus: EventBus,
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {
    private var allSchedules: List<TodoSchedule> = emptyList()

    init {
        viewModelScope.launch {
            val sortType = configRepository.getSortType()
            setState { copy(sortType = sortType) }
        }
    }

    internal suspend fun loadSchedules() {
        allSchedules = todoRepository.loadSchedules()

        val byDate = buildByDateMap(allSchedules, currentState.sortType)
        val byInfo = allSchedules.groupBy { it.infoId }

        setState {
            copy(
                isLoading = false,
                schedulesByDateMap = byDate,
                schedulesByTodoInfo = byInfo
            )
        }
    }

    override suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnAddTodoClick -> navigationBus.navigate(
                To(AddTodoRoute(intent.selectedDate.toFormattedString()))
            )

            is HomeIntent.OnCheckedChange -> onCheckedChange(intent.schedule)
            is HomeIntent.OnEditScheduleClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is HomeIntent.OnDelayScheduleClick -> onDelaySchedule(intent.schedule)
            is HomeIntent.OnDeleteScheduleClick -> onDeleteSchedule(intent.schedule)
            is HomeIntent.OnUpdateScheduleClick -> onUpdateScheduleClick(intent.schedule.id)
            is HomeIntent.OnSortTypeClick -> eventBus.sendEvent(ShowBottomSheet(intent.content))
            is HomeIntent.OnUpdateSortType -> onUpdateSortType(intent.sortType)
        }
    }

    private suspend fun onCheckedChange(schedule: TodoSchedule) {
        val newSchedule = schedule.copy(isDone = !schedule.isDone)
        todoRepository.updateTodo(newSchedule)

        setState {
            val updatedByInfo = schedulesByTodoInfo
                .mapValues { (infoId, list) ->
                    if (infoId == schedule.infoId) {
                        list.map { if (it.id == schedule.id) newSchedule else it }
                    } else list
                }

            val updatedByDate = schedulesByDateMap
                .mapValues { (_, list) ->
                    list.map { if (it.id == schedule.id) newSchedule else it }
                }

            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }
    }

    private suspend fun onDeleteSchedule(schedule: TodoSchedule) {
        todoRepository.deleteTodo(schedule)

        setState {
            val updatedByInfo = schedulesByTodoInfo
                .mapValues { (infoId, list) ->
                    if (infoId == schedule.infoId) list.filter { it.id != schedule.id }
                    else list
                }

            val updatedByDate = schedulesByDateMap
                .mapValues { (_, list) ->
                    list.filter { it.id != schedule.id }
                }

            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정을 지웠습니다."))
    }

    private suspend fun onDelaySchedule(schedule: TodoSchedule) {
        val nextDate = schedule.date.plusDays(1)
        val alreadyExistsOnNext = currentState
            .schedulesByDateMap[nextDate]
            ?.any { it.infoId == schedule.infoId } == true

        if (alreadyExistsOnNext) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정은 이미 다음 날에 있습니다."))
            eventBus.sendEvent(EbbingEvent.HideBottomSheet)
            return
        }

        val delayed = schedule.copy(date = nextDate)
        todoRepository.updateTodo(delayed)

        if (nextDate.isAfter(LocalDate.now())) {
            val triggerAtMillis = nextDate
                .atTime(LocalTime.of(18, 30))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            alarmScheduler.scheduleDailyExact(
                date = nextDate,
                triggerAtMillis = triggerAtMillis
            )
        }

        allSchedules = allSchedules.map { if (it.id == schedule.id) delayed else it }
        val newByDate = buildByDateMap(allSchedules, currentState.sortType)
        val newByInfo = allSchedules.groupBy { it.infoId }
        setState {
            copy(
                schedulesByDateMap = newByDate,
                schedulesByTodoInfo = newByInfo
            )
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정을 다음 날로 미뤘습니다."))
    }

    private suspend fun onUpdateScheduleClick(scheduleId: Int) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(To(EditTodoRoute(scheduleId)))
    }

    private suspend fun onUpdateSortType(sortType: SortType) {
        configRepository.setSortType(sortType)

        val byDate = buildByDateMap(allSchedules, sortType)
        setState {
            copy(
                sortType = sortType,
                schedulesByDateMap = byDate,
            )
        }
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }

    private fun buildByDateMap(
        schedules: List<TodoSchedule>,
        sortType: SortType,
    ): Map<LocalDate, List<TodoSchedule>> {
        val grouped = schedules.groupBy { it.date }

        return grouped.mapValues { (_, list) ->
            when (sortType) {
                SortType.CREATED -> list.sortedBy { it.createdAt }
                SortType.NAME -> list.sortedBy { it.title }
                SortType.PRIORITY -> list.sortedByDescending { it.priority }
            }
        }
    }
}
