package com.tgyuu.home.graph.edittodo

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.now
import com.tgyuu.common.toFormattedString
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.edittodo.contract.EditTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoState
import com.tgyuu.home.model.toUiModel
import com.tgyuu.home.model.toUiModels
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.TagGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import javax.inject.Inject
import kotlin.time.ExperimentalTime

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
            val originSchedule = todoRepository.loadSchedule(scheduleId) ?: run {
                navigationBus.navigate(NavigationEvent.Up)
                return@launch
            }

            val originTagDeferred = async { todoRepository.loadTag(originSchedule.tagId) }
            val sameInfoSchedulesDeferred =
                async { todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId) }
            val todoInfoDeferred = async { todoRepository.loadTodoInfoById(originSchedule.infoId) }

            val originTag = originTagDeferred.await() ?: run {
                navigationBus.navigate(NavigationEvent.Up)
                return@launch
            }
            val schedulesByDateMap = sameInfoSchedulesDeferred.await()
            val todoInfo = todoInfoDeferred.await()

            setState {
                copy(
                    originSchedule = originSchedule,
                    schedulesByDateMap = schedulesByDateMap
                        .groupBy { it.date }
                        .mapValues { (_, list) -> list.toUiModels() }
                        .toImmutableMap(),
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    priority = originSchedule.priority.takeIf { it != 0 }?.toString() ?: "",
                    tag = originTag.toUiModel(),
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTags()
        setState { copy(tagList = loadedTagList.toUiModels()) }
    }

    internal fun loadNewTag() {
        todoRepository.recentAddedTagId?.let {
            viewModelScope.launch {
                val newTag = todoRepository.loadTag(it.toInt()) ?: return@launch
                setState { copy(tag = newTag.toUiModel()) }
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

        val scheduledDates: Set<LocalDate> = currentState.schedulesByDateMap[date]
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

    private suspend fun onTagChange(todoTag: TodoTagUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
    }

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(TagGraph.AddTagRoute))
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        val tag = currentState.tag ?: return
        val newSchedule = currentState.originSchedule!!.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = tag.id,
            name = tag.name,
            color = tag.color,
            priority = currentState.priority?.toIntOrNull() ?: 0,
        )

        todoRepository.updateTodo(newSchedule)
        todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())
        val (hour, minute) = configRepository.getAlarmTime()

        currentState.originSchedule?.date?.let { originDate ->
            if (newSchedule.date != originDate) {
                // 새 날짜가 오늘 이후면 알람 재등록
                if (newSchedule.date > LocalDate.now()) {
                    val triggerAtMillis = newSchedule.date.run {
                        val dateTime = LocalDateTime(
                            year = this.year,
                            month = this.monthNumber,
                            day = this.day,
                            hour = hour,
                            minute = minute,
                            second = 0,
                            nanosecond = 0
                        )
                        dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    }

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
