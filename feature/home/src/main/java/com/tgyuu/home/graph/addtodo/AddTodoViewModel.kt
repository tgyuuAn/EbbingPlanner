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
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.model.toUiModel
import com.tgyuu.home.model.toUiModels
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.TagGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableSet
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

        setState {
            copy(
                selectedDate = dateStr.toLocalDateOrThrow(),
                tag = DefaultTodoTag.toUiModel(),
                repeatCycle = DefaultRepeatCycles.first().toUiModel(),
            )
        }
        initNotificationState()

        viewModelScope.launch {
            configRepository.getMondayStart()
                .collect { setState { copy(mondayStart = it) } }
        }
    }

    private fun initNotificationState() = viewModelScope.launch {
        val (hour, minute) = configRepository.getAlarmTime()
        val message = configRepository.getAlarmMessage()

        setState {
            copy(
                notificationState = notificationState.copy(
                    alarmHour = hour,
                    alarmMinute = minute,
                    message = message,
                    originMessage = message,
                )
            )
        }
    }

    internal fun loadNewTag() {
        todoRepository.recentAddedTagId?.let {
            viewModelScope.launch {
                val newTag = todoRepository.loadTag(it.toInt())
                setState { copy(tag = newTag.toUiModel()) }
            }
        }
    }

    internal fun loadNewRepeatCycle() {
        todoRepository.recentAddedRepeatCycleId?.let {
            viewModelScope.launch {
                val newRepeatCycle = todoRepository.loadRepeatCycle(it.toInt())
                setState { copy(repeatCycle = newRepeatCycle.toUiModel()) }
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTags()
        setState { copy(tagList = loadedTagList.toUiModels()) }
    }

    internal fun loadRepeatCycles() = viewModelScope.launch {
        val loadedRepeatCycleList = todoRepository.loadRepeatCycles()
        val allRepeatCycles = DefaultRepeatCycles + loadedRepeatCycleList
        setState { copy(repeatCycleList = allRepeatCycles.toUiModels()) }
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

            // Notification 관련 Intent
            AddTodoIntent.OnNotificationToggleClick -> onNotificationToggleClick()
            is AddTodoIntent.OnAlarmTimeChange -> onAlarmTimeChange(intent.hour, intent.minute)
            is AddTodoIntent.OnAlarmMessageChange -> onAlarmMessageChange(intent.message)
            AddTodoIntent.OnAlarmMessageReset -> onAlarmMessageReset()
            AddTodoIntent.OnNotificationBackClick -> onNotificationBackClick()
            AddTodoIntent.OnNotificationSaveClick -> onNotificationSaveClick()
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

    private suspend fun onTagChange(todoTag: TodoTagUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
    }

    private suspend fun onRepeatCycleChange(repeatCycle: RepeatCycleUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        eventBus.awaitBottomSheetHidden()
        setState { copy(repeatCycle = repeatCycle) }
    }

    private suspend fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays.toMutableSet()

        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("모든 요일을 휴식할 수는 없습니다"))
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
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

        val shouldShowNudge = configRepository.shouldShowNotificationNudge()

        if (shouldShowNudge) {
            setState { copy(page = AddTodoState.Page.NOTIFICATION) }
        } else {
            saveTodoAndNavigateHome()
        }
    }

    private suspend fun saveTodoAndNavigateHome() {
        val tag = currentState.tag ?: return
        todoRepository.addTodo(
            title = currentState.title,
            dates = currentState.schedules,
            tagId = tag.id,
            priority = currentState.priority?.toIntOrNull(),
            restDays = currentState.restDays.toSet(),
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

        val isFirstTodo = configRepository.markFirstTodoAdded()

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("새로운 일정을 추가하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(
                    workedDate = currentState.selectedDate.toFormattedString(),
                    showWidgetNudge = isFirstTodo,
                ),
                popUpTo = true,
            )
        )
    }

    private fun onNotificationToggleClick() {
        setState {
            copy(
                notificationState = notificationState.copy(
                    notificationEnabled = !notificationState.notificationEnabled
                )
            )
        }
    }

    private fun onAlarmTimeChange(hour: Int, minute: Int) {
        setState {
            copy(
                notificationState = notificationState.copy(
                    alarmHour = hour,
                    alarmMinute = minute
                )
            )
        }
    }

    private fun onAlarmMessageChange(message: String) {
        setState {
            copy(
                notificationState = notificationState.copy(message = message)
            )
        }
    }

    private fun onAlarmMessageReset() {
        setState {
            copy(
                notificationState = notificationState.copy(
                    message = DEFAULT_ALARM_MESSAGE
                )
            )
        }
    }

    private fun onNotificationBackClick() {
        setState { copy(page = AddTodoState.Page.ADD_TODO) }
    }

    private suspend fun onNotificationSaveClick() {
        val notificationState = currentState.notificationState

        configRepository.setNotificationEnabled(notificationState.notificationEnabled)

        if (notificationState.notificationEnabled) {
            configRepository.updateAlarmTime(
                notificationState.alarmHour.toString(),
                notificationState.alarmMinute.toString()
            )
            configRepository.updateAlarmMessage(notificationState.message)
        }

        saveTodoAndNavigateHome()
    }

}
