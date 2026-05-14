package com.tgyuu.dashboard

import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent.HideBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowSnackBar
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.dashboard.model.toDomainModel
import com.tgyuu.dashboard.model.toUiModel
import com.tgyuu.dashboard.model.toUiModels
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.EditDateRoute
import com.tgyuu.navigation.HomeGraph.EditTodoRoute
import com.tgyuu.navigation.MemoGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentHashSet
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val navigationBus: NavigationBus,
    internal val eventBus: EventBus,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    internal suspend fun loadTodoSchedules() = coroutineScope {
        suspendRunCatching {
            val allSchedules = todoRepository.loadAllSchedules()

            val infosByTagMap = allSchedules
                .groupBy { it.tagId }
                .mapValues { (_, schedules) ->
                    schedules.map { s ->
                        TodoInfo(id = s.infoId, title = s.title, tagId = s.tagId).toUiModel()
                    }.distinctBy { it.id }.toImmutableList()
                }.toImmutableMap()

            val schedulesByInfoMap = allSchedules
                .groupBy { it.infoId }
                .mapValues { (_, schedules) ->
                    schedules.map { s ->
                        TodoSchedule(
                            id = s.id, infoId = s.infoId, title = s.title,
                            tagId = s.tagId, name = s.name, color = s.color,
                            date = s.date, memo = s.memo, priority = s.priority,
                            isDone = s.isDone, createdAt = s.createdAt,
                            infoCreatedAt = s.createdAt,
                        ).toUiModel()
                    }.sortedBy { it.date }.toImmutableList()
                }.toImmutableMap()

            val todoTags = todoRepository.loadTags()

            setState {
                copy(
                    infosByTagMap = infosByTagMap,
                    schedulesByInfoMap = schedulesByInfoMap,
                    tags = todoTags.toUiModels(),
                )
            }
        }
    }

    override suspend fun processIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.OnToggleTagExpand -> onToggleTagExpand(intent.tagId)
            is ScheduleIntent.OnToggleInfoExpand -> onToggleInfoExpand(intent.infoId)
            is ScheduleIntent.OnScheduleClick -> onScheduleClick(intent.schedule)
            is ScheduleIntent.OnNavigateToAddTodo -> onNavigateToAddTodo()
            is ScheduleIntent.OnShowBottomSheet -> eventBus.sendEvent(ShowBottomSheet(intent.content))
            is ScheduleIntent.OnSaveTag -> onSaveTag(intent.tagId, intent.name, intent.color)
            is ScheduleIntent.OnDeleteTag -> onDeleteTag(intent.tagId)
            is ScheduleIntent.OnUpdateInfoClick -> onUpdateInfoClick(intent.schedule)
            is ScheduleIntent.OnUpdateDateClick -> onUpdateDateClick(intent.schedule)
            is ScheduleIntent.OnDeleteSingleClick -> onDeleteSingle(intent.schedule)
            is ScheduleIntent.OnDeleteRemainingClick -> onDeleteRemaining(intent.schedule)
            is ScheduleIntent.OnDelaySingleClick -> onDelaySingle(intent.schedule)
            is ScheduleIntent.OnDelayAllClick -> onDelayAll(intent.schedule)
            is ScheduleIntent.OnMemoClick -> onMemoClick(intent.schedule)
            is ScheduleIntent.OnDeleteMemoClick -> onDeleteMemo(intent.schedule)
        }
    }

    private suspend fun onNavigateToAddTodo() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "AddTodo")
        )
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        navigationBus.navigate(To(AddTodoRoute(today)))
    }

    private fun onToggleTagExpand(tagId: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ToggleTag")
        )
        val current = currentState.expandedTagIds.toMutableSet()
        if (tagId in current) current.remove(tagId) else current.add(tagId)
        setState { copy(expandedTagIds = current.toPersistentHashSet()) }
    }

    private fun onToggleInfoExpand(infoId: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ToggleInfo")
        )
        val current = currentState.expandedInfoIds.toMutableSet()
        if (infoId in current) current.remove(infoId) else current.add(infoId)
        setState { copy(expandedInfoIds = current.toPersistentHashSet()) }
    }

    private suspend fun onScheduleClick(schedule: TodoScheduleUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Schedule")
        )
        val domainSchedule = schedule.toDomainModel()
        todoRepository.updateTodo(domainSchedule.copy(isDone = !domainSchedule.isDone))
        loadTodoSchedules()
    }

    private suspend fun onSaveTag(tagId: Int, name: String, color: Int) {
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.updateTag(
                TodoTag(id = tag.id, name = name, color = color, createdAt = tag.createdAt)
            )
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("태그를 수정하였습니다"))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteTag(tagId: Int) {
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.deleteTag(
                TodoTag(id = tag.id, name = tag.name, color = tag.color, createdAt = tag.createdAt)
            )
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("태그를 삭제하였습니다"))
            loadTodoSchedules()
        }
    }

    private suspend fun onUpdateInfoClick(schedule: TodoScheduleUiModel) {
        eventBus.sendEvent(HideBottomSheet)
        navigationBus.navigate(To(EditTodoRoute(schedule.id)))
    }

    private suspend fun onUpdateDateClick(schedule: TodoScheduleUiModel) {
        eventBus.sendEvent(HideBottomSheet)
        navigationBus.navigate(To(EditDateRoute(schedule.infoId)))
    }

    private suspend fun onDeleteSingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            todoRepository.deleteTodo(schedule.toDomainModel())
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("해당 일정을 지웠습니다."))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteRemaining(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelete = allSchedules.filter { !it.date.isBefore(schedule.date) }
            toDelete.forEach { todoRepository.deleteTodo(it) }
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("해당 일정 이후 연계된 일정들을 모두 지웠습니다."))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelaySingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(domainSchedule.copy(date = domainSchedule.date.plusDays(1)))
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("해당 일정을 다음 날로 미뤘습니다."))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelayAll(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelay = allSchedules.filter { !it.date.isBefore(schedule.date) }
            val updated = toDelay.map { it.copy(date = it.date.plusDays(1)) }
            todoRepository.updateTodos(updated)
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("${updated.size}개 일정을 미뤘습니다."))
            loadTodoSchedules()
        }
    }

    private suspend fun onMemoClick(schedule: TodoScheduleUiModel) {
        eventBus.sendEvent(HideBottomSheet)
        val destination = if (schedule.memo.originalText.isEmpty()) {
            MemoGraph.AddMemoRoute(schedule.id)
        } else {
            MemoGraph.EditMemoRoute(schedule.id)
        }
        navigationBus.navigate(To(destination))
    }

    private suspend fun onDeleteMemo(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(domainSchedule.copy(memo = ""))
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar("메모를 제거하였습니다"))
            loadTodoSchedules()
        }
    }

    companion object {
        private const val SCREEN_NAME = "Schedule"
    }
}
