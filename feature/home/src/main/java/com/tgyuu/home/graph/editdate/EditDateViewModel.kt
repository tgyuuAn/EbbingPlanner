package com.tgyuu.home.graph.editdate

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.editdate.contract.EditDateIntent
import com.tgyuu.home.graph.editdate.contract.EditDateState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@HiltViewModel
class EditDateViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditDateState, EditDateIntent>(EditDateState()) {
    private var originSchedules: List<TodoSchedule> = emptyList()

    init {
        viewModelScope.launch {
            val infoId = savedStateHandle.get<Int>("infoId")
                ?: throw IllegalArgumentException("해당 일정의 정보가 없습니다.")

            val result = todoRepository.loadSchedulesByTodoInfo(infoId)
            result.firstOrNull()?.let {
                setState {
                    copy(
                        title = it.title,
                        originTagColor = it.color,
                        selectedDate = it.date,
                    )
                }
            }

            originSchedules = result
        }
    }

    internal fun loadNewRepeatCycle() {
        todoRepository.recentAddedRepeatCycleId?.let {
            viewModelScope.launch {
                val newRepeatCycle = todoRepository.loadRepeatCycle(it.toInt())
                setState { copy(repeatCycle = newRepeatCycle) }
            }
        }
    }

    internal fun loadRepeatCycles() = viewModelScope.launch {
        val loadedRepeatCycleList = todoRepository.loadRepeatCycles()

        setState { copy(repeatCycleList = DefaultRepeatCycles + loadedRepeatCycleList) }
    }

    override suspend fun processIntent(intent: EditDateIntent) {
        when (intent) {
            EditDateIntent.OnBackClick -> navigationBus.navigate(
                NavigationEvent.To(
                    route = HomeRoute(currentState.selectedDate.toFormattedString()),
                    popUpTo = true,
                )
            )

            is EditDateIntent.OnSelectedDataChangeClick ->
                eventBus.sendEvent(ShowBottomSheet(intent.content))

            is EditDateIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditDateIntent.OnRepeatCycleDropDownClick ->
                eventBus.sendEvent(ShowBottomSheet(intent.content))

            is EditDateIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is EditDateIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is EditDateIntent.OnSaveClick -> onSaveClick(intent.isDoneSchedule)
            EditDateIntent.OnAddRepeatCycleClick -> onAddRepeatCycleClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(selectedDate = date) }
    }

    private suspend fun onRepeatCycleChange(repeatCycle: RepeatCycle) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(repeatCycle = repeatCycle) }
    }

    private suspend fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays

        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("모든 요일을 휴식할 수는 없습니다"))
            return
        }

        setState { copy(restDays = newRestDays) }
    }

    private suspend fun onAddRepeatCycleClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(RepeatCycleGraph.AddRepeatCycleRoute))
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun onSaveClick(isDoneSchedules: List<Boolean>) {
        if (currentState.schedules.isEmpty()) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("저장할 일정이 없습니다"))
            return
        }

        originSchedules.forEach { alarmScheduler.cancelDailyExact(it.date) }

        originSchedules.firstOrNull()
            ?.infoId
            ?.let { todoRepository.deleteTodoByTodoInfo(it) }

        todoRepository.addTodo(
            title = currentState.title,
            dates = currentState.schedules,
            isDoneSchedules = isDoneSchedules,
            tagId = originSchedules.firstOrNull()?.tagId ?: 0,
            priority = originSchedules.firstOrNull()?.priority,
        )

        val (hour, minute) = configRepository.getAlarmTime()
        currentState.schedules.forEach { schedule ->
            try {
                val triggerAtMillis = schedule.run {
                    val dateTime = LocalDateTime(
                        year = this.year,
                        month = this.month.number,
                        day = this.day,
                        hour = hour,
                        minute = minute,
                        second = 0,
                        nanosecond = 0
                    )
                    dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                }

                if (triggerAtMillis <= System.currentTimeMillis()) return@forEach

                alarmScheduler.scheduleDailyExact(
                    date = schedule,
                    triggerAtMillis = triggerAtMillis,
                )
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "알람 등록 실패: $schedule", e)
            }
        }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("해당 일정의 날짜 및 반복 주기를 변경하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(currentState.selectedDate.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
