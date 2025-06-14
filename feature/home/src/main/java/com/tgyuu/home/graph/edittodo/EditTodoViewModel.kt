package com.tgyuu.home.graph.edittodo

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.edittodo.contract.EditTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoState
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.TagGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val alarmScheduler: AlarmScheduler,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)

            val originTagDeferred = async { todoRepository.loadTag(originSchedule.tagId) }
            val sameInfoSchedulesDeferred =
                async { todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId) }

            val originTag = originTagDeferred.await()
            val schedulesByDateMap = sameInfoSchedulesDeferred.await()

            setState {
                copy(
                    originSchedule = originSchedule,
                    schedulesByDateMap = schedulesByDateMap.groupBy { it.date },
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    priority = originSchedule.priority.takeIf { it != 0 }?.toString() ?: "",
                    tag = originTag,
                )
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTags()
        setState { copy(tagList = loadedTagList) }
    }

    internal fun loadNewTag() {
        todoRepository.recentAddedTagId?.let {
            viewModelScope.launch {
                val newTag = todoRepository.loadTag(it.toInt())
                setState { copy(tag = newTag) }
            }
        }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> navigationBus.navigate(
                NavigationEvent.To(
                    route = HomeGraph.HomeRoute(currentState.selectedDate.toFormattedString()),
                    popUpTo = true,
                )
            )

            is EditTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            is EditTodoIntent.OnTagDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnTagChange -> onTagChange(intent.tag)
            EditTodoIntent.OnAddTagClick -> onAddTagClick()
            EditTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        if (date == currentState.selectedDate) return

        val scheduledDates: Set<LocalDate> =
            currentState.schedulesByDateMap[date]
                ?.map { it.date }
                ?.toSet()
                ?: emptySet()

        if (date in scheduledDates) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("이미 해당 날짜에 일정이 있습니다."))
            eventBus.sendEvent(EbbingEvent.HideBottomSheet)
            return
        }

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

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(TagGraph.AddTagRoute))
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        val newSchedule = currentState.originSchedule!!.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = currentState.tag.id,
            name = currentState.tag.name,
            color = currentState.tag.color,
            priority = currentState.priority?.toIntOrNull() ?: 0,
        )

        todoRepository.updateTodo(newSchedule)
        val (hour, minute) = configRepository.getAlarmTime()

        currentState.originSchedule?.date?.let { originDate ->
            if (newSchedule.date != originDate) {
                // 새 날짜가 오늘 이후면 알람 재등록
                if (newSchedule.date.isAfter(LocalDate.now())) {
                    val triggerAtMillis = newSchedule.date
                        .atTime(LocalTime.of(hour, minute))
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    alarmScheduler.scheduleDailyExact(
                        date = newSchedule.date,
                        triggerAtMillis = triggerAtMillis
                    )
                }
            }
        }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("일정을 업데이트 하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeGraph.HomeRoute(currentState.selectedDate.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
