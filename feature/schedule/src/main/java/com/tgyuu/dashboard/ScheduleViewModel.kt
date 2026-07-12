package com.tgyuu.dashboard

import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent.HideBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowSnackBar
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.now
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.dashboard.model.toDomainModel
import com.tgyuu.dashboard.model.toUiModel
import com.tgyuu.dashboard.model.toUiModels
import com.tgyuu.designsystem.R
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentHashSet
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class ScheduleViewModel(
    private val todoRepository: TodoRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val resourceProvider: ResourceProvider,
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
                            date = s.date, memo = s.memo, isPinned = s.isPinned,
                            isDone = s.isDone, createdAt = s.createdAt,
                            infoCreatedAt = s.createdAt,
                        ).toUiModel()
                    }.sortedBy { it.date }.toImmutableList()
                }.toImmutableMap()

            val todoTags = todoRepository.loadTags()
            val tags = todoTags.toUiModels()

            val infoScheduleCountMap = schedulesByInfoMap
                .mapValues { it.value.size }.toImmutableMap()

            val infoAchievementRateMap = schedulesByInfoMap
                .mapValues { (_, schedules) ->
                    if (schedules.isEmpty()) 0f
                    else schedules.count { it.isDone }.toFloat() / schedules.size
                }.toImmutableMap()

            val infoAllDoneMap = schedulesByInfoMap
                .mapValues { (_, schedules) ->
                    schedules.isNotEmpty() && schedules.all { it.isDone }
                }.toImmutableMap()

            val tagScheduleCountMap = tags.associate { tag ->
                val infos = infosByTagMap[tag.id].orEmpty()
                tag.id to infos.sumOf { info -> schedulesByInfoMap[info.id]?.size ?: 0 }
            }.toImmutableMap()

            val tagAchievementRateMap = tags.associate { tag ->
                val infos = infosByTagMap[tag.id].orEmpty()
                val allSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
                val rate = if (allSchedules.isEmpty()) 0f
                else allSchedules.count { it.isDone }.toFloat() / allSchedules.size
                tag.id to rate
            }.toImmutableMap()

            val tagAllDoneMap = tags.associate { tag ->
                val infos = infosByTagMap[tag.id].orEmpty()
                val allSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
                tag.id to (allSchedules.isNotEmpty() && allSchedules.all { it.isDone })
            }.toImmutableMap()

            val visibleTags = tags
                .filter { (tagScheduleCountMap[it.id] ?: 0) > 0 }
                .toImmutableList()

            setState {
                copy(
                    tags = tags,
                    infosByTagMap = infosByTagMap,
                    schedulesByInfoMap = schedulesByInfoMap,
                    infoScheduleCountMap = infoScheduleCountMap,
                    infoAchievementRateMap = infoAchievementRateMap,
                    infoAllDoneMap = infoAllDoneMap,
                    tagScheduleCountMap = tagScheduleCountMap,
                    tagAchievementRateMap = tagAchievementRateMap,
                    tagAllDoneMap = tagAllDoneMap,
                    visibleTags = visibleTags,
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
            is ScheduleIntent.OnReplaceBottomSheet -> onReplaceBottomSheet(intent.content)
            is ScheduleIntent.OnSaveTag -> onSaveTag(intent.tagId, intent.name, intent.color)
            is ScheduleIntent.OnDeleteTag -> onDeleteTag(intent.tagId)
            is ScheduleIntent.OnRequestDeleteTag -> onRequestDeleteTag(intent.tagId, intent.tagName)
            is ScheduleIntent.OnClearPendingDeleteTag -> setState { copy(pendingDeleteTag = null) }
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

    private suspend fun onReplaceBottomSheet(content: BottomSheetContent) {
        eventBus.sendEvent(HideBottomSheet)
        eventBus.awaitBottomSheetHidden()
        eventBus.sendEvent(ShowBottomSheet(content))
    }

    private suspend fun onRequestDeleteTag(tagId: Int, tagName: String) {
        eventBus.sendEvent(HideBottomSheet)
        eventBus.awaitBottomSheetHidden()
        setState { copy(pendingDeleteTag = tagId to tagName) }
    }

    private suspend fun onNavigateToAddTodo() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "AddSchedule")
        )
        val today = LocalDate.now().toFormattedString()
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
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditTag")
        )
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.updateTag(
                TodoTag(id = tag.id, name = name, color = color, createdAt = tag.createdAt)
            )
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_tag_updated)))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteTag(tagId: Int) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "DeleteTag")
        )
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.deleteTag(
                TodoTag(id = tag.id, name = tag.name, color = tag.color, createdAt = tag.createdAt)
            )
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_tag_deleted)))
            loadTodoSchedules()
        }
    }

    private suspend fun onUpdateInfoClick(schedule: TodoScheduleUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditTodo")
        )
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
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_schedule_deleted)))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteRemaining(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelete = allSchedules.filter { it.date >= schedule.date }
            toDelete.forEach { todoRepository.deleteTodo(it) }
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_remaining_deleted)))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelaySingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(
                domainSchedule.copy(date = domainSchedule.date.plus(1, DateTimeUnit.DAY))
            )
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_delayed_single)))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelayAll(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelay = allSchedules.filter { it.date >= schedule.date }
            val updated = toDelay.map { it.copy(date = it.date.plus(1, DateTimeUnit.DAY)) }
            todoRepository.updateTodos(updated)
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(
                ShowSnackBar(
                    resourceProvider.getString(
                        R.string.schedule_snackbar_delayed_all,
                        updated.size,
                    )
                )
            )
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
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_memo_removed)))
            loadTodoSchedules()
        }
    }

    companion object {
        private const val SCREEN_NAME = "Schedule"
    }
}
