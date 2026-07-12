package com.tgyuu.shared.ui.feature.schedule

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.now
import com.tgyuu.shared.common.suspendRunCatching
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.domain.model.TodoInfo
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentHashSet
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_delayed_single
import ebbingplanner.shared.generated.resources.snack_memo_removed
import ebbingplanner.shared.generated.resources.snack_remaining_deleted
import ebbingplanner.shared.generated.resources.snack_schedule_deleted
import ebbingplanner.shared.generated.resources.snack_schedules_delayed
import ebbingplanner.shared.generated.resources.snack_tag_deleted
import ebbingplanner.shared.generated.resources.snack_tag_updated
import org.jetbrains.compose.resources.getString

class ScheduleViewModel(
    private val todoRepository: TodoRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val onNavigateToAddTodo: (date: String) -> Unit,
    private val onNavigateToEditTodo: (scheduleId: Int) -> Unit,
    private val onNavigateToEditDate: (infoId: Int) -> Unit,
    private val onNavigateToMemo: (scheduleId: Int) -> Unit,
    private val onNavigateToEditMemo: (scheduleId: Int) -> Unit,
    private val onShowSnackBar: (String) -> Unit,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    suspend fun loadTodoSchedules() {
        suspendRunCatching {
            val allSchedules = todoRepository.loadAllSchedules()

            val infosByTagMap = allSchedules
                .groupBy { it.tagId }
                .mapValues { (_, schedules) ->
                    schedules.map { s ->
                        TodoInfoUiModel(id = s.infoId, title = s.title, tagId = s.tagId)
                    }.distinctBy { it.id }.toImmutableList()
                }.toImmutableMap()

            val schedulesByInfoMap = allSchedules
                .groupBy { it.infoId }
                .mapValues { (_, schedules) ->
                    schedules.map { it.toUiModel() }
                        .sortedBy { it.date }
                        .toImmutableList()
                }.toImmutableMap()

            val todoTags = todoRepository.loadTags()
            val tags = todoTags.map { it.toUiModel() }.toImmutableList()

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
                val tagSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
                val rate = if (tagSchedules.isEmpty()) 0f
                else tagSchedules.count { it.isDone }.toFloat() / tagSchedules.size
                tag.id to rate
            }.toImmutableMap()

            val tagAllDoneMap = tags.associate { tag ->
                val infos = infosByTagMap[tag.id].orEmpty()
                val tagSchedules = infos.flatMap { schedulesByInfoMap[it.id].orEmpty() }
                tag.id to (tagSchedules.isNotEmpty() && tagSchedules.all { it.isDone })
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
            is ScheduleIntent.OnSaveTag -> onSaveTag(intent.tagId, intent.name, intent.color)
            is ScheduleIntent.OnDeleteTag -> onDeleteTag(intent.tagId)
            is ScheduleIntent.OnRequestDeleteTag -> setState { copy(pendingDeleteTag = intent.tagId to intent.tagName) }
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

    private fun onNavigateToAddTodo() {
        logClick("AddSchedule")
        val today = LocalDate.now().toFormattedString()
        onNavigateToAddTodo.invoke(today)
    }

    private fun onToggleTagExpand(tagId: Int) {
        logClick("ToggleTag")
        val current = currentState.expandedTagIds.toMutableSet()
        if (tagId in current) current.remove(tagId) else current.add(tagId)
        setState { copy(expandedTagIds = current.toPersistentHashSet()) }
    }

    private fun onToggleInfoExpand(infoId: Int) {
        logClick("ToggleInfo")
        val current = currentState.expandedInfoIds.toMutableSet()
        if (infoId in current) current.remove(infoId) else current.add(infoId)
        setState { copy(expandedInfoIds = current.toPersistentHashSet()) }
    }

    private suspend fun onScheduleClick(schedule: TodoScheduleUiModel) {
        logClick("Schedule")
        val domainSchedule = schedule.toDomainModel()
        todoRepository.updateTodo(domainSchedule.copy(isDone = !domainSchedule.isDone))
        loadTodoSchedules()
    }

    private suspend fun onSaveTag(tagId: Int, name: String, color: Int) {
        logClick("EditTag")
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.updateTag(
                TodoTag(id = tag.id, name = name, color = color, createdAt = tag.createdAt)
            )
            onShowSnackBar(getString(Res.string.snack_tag_updated))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteTag(tagId: Int) {
        logClick("DeleteTag")
        val tag = currentState.tags.firstOrNull { it.id == tagId } ?: return
        suspendRunCatching {
            todoRepository.deleteTag(
                TodoTag(id = tag.id, name = tag.name, color = tag.color, createdAt = tag.createdAt)
            )
            onShowSnackBar(getString(Res.string.snack_tag_deleted))
            loadTodoSchedules()
        }
    }

    private fun onUpdateInfoClick(schedule: TodoScheduleUiModel) {
        logClick("EditTodo")
        onNavigateToEditTodo(schedule.id)
    }

    private fun onUpdateDateClick(schedule: TodoScheduleUiModel) {
        onNavigateToEditDate(schedule.infoId)
    }

    private suspend fun onDeleteSingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            todoRepository.deleteTodo(schedule.toDomainModel())
            onShowSnackBar(getString(Res.string.snack_schedule_deleted))
            loadTodoSchedules()
        }
    }

    private suspend fun onDeleteRemaining(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelete = allSchedules.filter { it.date >= schedule.date }
            toDelete.forEach { todoRepository.deleteTodo(it) }
            onShowSnackBar(getString(Res.string.snack_remaining_deleted))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelaySingle(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(
                domainSchedule.copy(date = domainSchedule.date.plus(1, DateTimeUnit.DAY))
            )
            onShowSnackBar(getString(Res.string.snack_delayed_single))
            loadTodoSchedules()
        }
    }

    private suspend fun onDelayAll(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val allSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
            val toDelay = allSchedules.filter { it.date >= schedule.date }
            toDelay.forEach { s ->
                todoRepository.updateTodo(s.copy(date = s.date.plus(1, DateTimeUnit.DAY)))
            }
            onShowSnackBar(getString(Res.string.snack_schedules_delayed, toDelay.size))
            loadTodoSchedules()
        }
    }

    private suspend fun onMemoClick(schedule: TodoScheduleUiModel) {
        if (schedule.memo.isEmpty()) {
            onNavigateToMemo(schedule.id)
        } else {
            onNavigateToEditMemo(schedule.id)
        }
    }

    private suspend fun onDeleteMemo(schedule: TodoScheduleUiModel) {
        suspendRunCatching {
            val domainSchedule = schedule.toDomainModel()
            todoRepository.updateTodo(domainSchedule.copy(memo = ""))
            onShowSnackBar(getString(Res.string.snack_memo_removed))
            loadTodoSchedules()
        }
    }

    private fun logClick(buttonName: String) {
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.BUTTON_CLICK,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to SCREEN_NAME,
                    AnalyticsEvent.PropertiesKeys.BUTTON_NAME to buttonName,
                ),
            )
        )
    }

    private fun TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id, infoId = infoId, title = title, tagId = tagId,
        name = name, color = color, date = date, memo = memo,
        isPinned = isPinned, isDone = isDone, createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun TodoScheduleUiModel.toDomainModel() = TodoSchedule(
        id = id, infoId = infoId, title = title, tagId = tagId,
        name = name, color = color, date = date, memo = memo,
        isPinned = isPinned, isDone = isDone, createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun TodoTag.toUiModel() = TodoTagUiModel(
        id = id, name = name, color = color, createdAt = createdAt,
    )

    companion object {
        private const val SCREEN_NAME = "Schedule"
    }
}
