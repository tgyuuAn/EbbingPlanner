package com.tgyuu.home.graph.edittodo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
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
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "EditTodo",
            )
        )

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
                    isPinned = originSchedule.isPinned,
                    tag = originTag.toUiModel(),
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
        }

        viewModelScope.launch {
            configRepository.getMondayStart()
                .collect { setState { copy(mondayStart = it) } }
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
            EditTodoIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "EditTodo", buttonName = "Back")
                )
                navigationBus.navigate(
                    NavigationEvent.To(
                        route = HomeGraph.HomeRoute(currentState.selectedDate.toFormattedString()),
                        popUpTo = true,
                    )
                )
            }

            is EditTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPinnedChange -> onPinnedChange(intent.isPinned)
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
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.home_snackbar_date_already_has_schedule)))
            eventBus.sendEvent(EbbingEvent.HideBottomSheet)
            return
        }

        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onPinnedChange(isPinned: Boolean) {
        setState { copy(isPinned = isPinned) }
    }

    private suspend fun onTagChange(todoTag: TodoTagUiModel) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
    }

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(TagGraph.AddTagRoute))
    }

    private suspend fun onSaveClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "EditTodo",
                buttonName = "Save",
            )
        )

        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.home_snackbar_required_fields)))
            return
        }

        val tag = currentState.tag ?: return
        val newSchedule = currentState.originSchedule!!.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = tag.id,
            name = tag.name,
            color = tag.color,
            isPinned = currentState.isPinned,
        )

        todoRepository.updateTodo(newSchedule)
        todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())
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

        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.home_snackbar_todo_updated)))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeGraph.HomeRoute(currentState.selectedDate.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
