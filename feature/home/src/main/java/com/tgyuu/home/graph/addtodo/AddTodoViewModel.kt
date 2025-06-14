package com.tgyuu.home.graph.addtodo

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.TagGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState()) {

    init {
        val dateStr = savedStateHandle.get<String>("selectedDate")
            ?: throw IllegalArgumentException("선택된 날짜가 없습니다.")

        setState { copy(selectedDate = dateStr.toLocalDateOrThrow()) }
    }

    internal fun loadNewTag() {
        todoRepository.recentAddedTagId?.let {
            viewModelScope.launch {
                val newTag = todoRepository.loadTag(it.toInt())
                setState { copy(tag = newTag) }
            }
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

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTags()
        setState { copy(tagList = loadedTagList) }
    }

    internal fun loadRepeatCycles() = viewModelScope.launch {
        val loadedRepeatCycleList = todoRepository.loadRepeatCycles()

        setState { copy(repeatCycleList = DefaultRepeatCycles + loadedRepeatCycleList) }
    }

    override suspend fun processIntent(intent: AddTodoIntent) {
        when (intent) {
            AddTodoIntent.OnBackClick -> navigationBus.navigate(
                NavigationEvent.To(
                    route = HomeRoute(currentState.selectedDate.toFormattedString()),
                    popUpTo = true,
                )
            )

            is AddTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is AddTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is AddTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            is AddTodoIntent.OnRepeatCycleDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is AddTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is AddTodoIntent.OnTagDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnTagChange -> onTagChange(intent.tag)
            AddTodoIntent.OnAddTagClick -> onAddTagClick()
            AddTodoIntent.OnSaveClick -> onSaveClick()
            AddTodoIntent.OnAddRepeatCycleClick -> onAddRepeatCycleClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onPriorityChange(priority: String) {
        if (!priority.isDigitsOnly()) return
        if (priority.length >= 4) return

        setState { copy(priority = priority) }
    }

    private suspend fun onTagChange(todoTag: TodoTag) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
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

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(TagGraph.AddTagRoute))
    }

    private suspend fun onAddRepeatCycleClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(RepeatCycleGraph.AddRepeatCycleRoute))
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        todoRepository.addTodo(
            title = currentState.title,
            dates = currentState.schedules,
            tagId = currentState.tag.id,
            priority = currentState.priority?.toIntOrNull(),
        )

        val (hour, minute) = configRepository.getAlarmTime()

        currentState.schedules.forEach { schedule ->
            try {
                val triggerAtMillis = schedule
                    .atTime(LocalTime.of(hour, minute))
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (triggerAtMillis <= System.currentTimeMillis()) return@forEach

                alarmScheduler.scheduleDailyExact(
                    date = schedule,
                    triggerAtMillis = triggerAtMillis,
                )
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "알람 등록 실패: $schedule", e)
            }
        }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("새로운 일정을 추가하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(currentState.selectedDate.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
