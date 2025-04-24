package com.tgyuu.home.graph.main

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.home.graph.main.contract.SortType
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.EditTodoRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {

    internal suspend fun loadSchedules() {
        val schedules = todoRepository.loadSchedules()
        val todosByDate: Map<LocalDate, List<TodoSchedule>> = schedules.groupBy { it.date }
        val todosByInfo: Map<Int, List<TodoSchedule>> = schedules.groupBy { it.infoId }

        setState {
            copy(
                isLoading = false,
                schedulesByDateMap = todosByDate,
                schedulesByTodoInfo = todosByInfo
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

        setState {
            val updatedByInfo = schedulesByTodoInfo
                .mapValues { (_, list) ->
                    list.map { if (it.id == schedule.id) delayed else it }
                }
            val removed =
                schedulesByDateMap[schedule.date]?.filter { it.id != schedule.id } ?: emptyList()
            val added = (schedulesByDateMap[nextDate] ?: emptyList()) + delayed
            val updatedByDate = schedulesByDateMap
                .toMutableMap()
                .apply {
                    this[schedule.date] = removed
                    this[nextDate] = added
                }

            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
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
        setState { copy(sortType = sortType) }
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }
}
