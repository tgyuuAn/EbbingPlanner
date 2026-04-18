package com.tgyuu.dashboard

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent.HideBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EbbingEvent.ShowSnackBar
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.dashboard.model.toDomainModel
import com.tgyuu.dashboard.model.toUiModel
import com.tgyuu.dashboard.model.toUiModels
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentHashSet
import kotlinx.coroutines.coroutineScope

class ScheduleViewModel(
    private val todoRepository: TodoRepository,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    internal suspend fun loadTodoSchedules() = coroutineScope {
        suspendRunCatching {
            val allSchedules = todoRepository.loadAllSchedules()

            val todoInfoMap = allSchedules.groupBy { schedule ->
                schedule.tagId
            }.mapValues { entry ->
                entry.value.map { schedule ->
                    TodoInfoUiModel(
                        id = schedule.infoId,
                        title = schedule.title,
                        tagId = schedule.tagId,
                    )
                }.distinctBy { it.id }.toImmutableList()
            }.toImmutableMap()

            val todoScheduleMap = allSchedules.groupBy { schedule ->
                schedule.infoId
            }.mapValues { entry ->
                entry.value.map { schedule ->
                    schedule.toUiModel()
                }.distinctBy { it.id }.toImmutableList()
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

    private fun onTagClick(tag: TodoTagUiModel) {
        setSelectedTag(tag)
    }

    private fun onInfoClick(todoInfo: TodoInfoUiModel) {
        setSelectedTodoInfo(todoInfo)
    }

    private suspend fun onScheduleClick(schedule: TodoScheduleUiModel) {
        onCheckedChange(schedule)
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
            val toDelete = allSchedules.filter { !it.date.isBefore(schedule.date) }
            toDelete.forEach { todoRepository.deleteTodo(it) }
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_remaining_deleted)))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelaySingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(domainSchedule.copy(date = domainSchedule.date.plusDays(1)))
            eventBus.sendEvent(HideBottomSheet)
            eventBus.sendEvent(ShowSnackBar(resourceProvider.getString(R.string.schedule_snackbar_delayed_single)))
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

    private suspend fun onCheckedChange(schedule: TodoScheduleUiModel) {
        val domainSchedule = schedule.toDomainModel()
        val newDomainSchedule = domainSchedule.copy(isDone = !domainSchedule.isDone)
        todoRepository.updateTodo(newDomainSchedule)

        val newSchedule = newDomainSchedule.toUiModel()
        val updatedMap = currentState.todoScheduleMap.toMutableMap()
        val schedules = updatedMap[schedule.infoId].orEmpty().map {
            if (it.id == schedule.id) newSchedule else it
        }.toImmutableList()
        updatedMap[schedule.infoId] = schedules
        setState { copy(todoScheduleMap = updatedMap.toImmutableMap()) }
    }
}
