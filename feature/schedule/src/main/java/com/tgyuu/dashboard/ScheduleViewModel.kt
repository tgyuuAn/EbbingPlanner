package com.tgyuu.dashboard

import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.dashboard.model.toDomainModel
import com.tgyuu.dashboard.model.toUiModel
import com.tgyuu.dashboard.model.toUiModels
import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    internal suspend fun loadTodoSchedules() = coroutineScope {
        suspendRunCatching {
            val allSchedules = todoRepository.loadAllSchedules()

            val todoInfoMap = allSchedules.groupBy { schedule ->
                schedule.tagId
            }.mapValues { entry ->
                entry.value.map { schedule ->
                    TodoInfo(
                        id = schedule.infoId,
                        title = schedule.title,
                        tagId = schedule.tagId,
                    ).toUiModel()
                }.distinctBy { it.id }.toImmutableList()
            }.toImmutableMap()

            val todoScheduleMap = allSchedules.groupBy { schedule ->
                schedule.infoId
            }.mapValues { entry ->
                entry.value.map { schedule ->
                    TodoSchedule(
                        id = schedule.id,
                        infoId = schedule.infoId,
                        title = schedule.title,
                        tagId = schedule.tagId,
                        name = schedule.name,
                        color = schedule.color,
                        date = schedule.date,
                        memo = schedule.memo,
                        priority = schedule.priority,
                        isDone = schedule.isDone,
                        createdAt = schedule.createdAt,
                        infoCreatedAt = schedule.createdAt
                    ).toUiModel()
                }.distinctBy { it.id }.toImmutableList()
            }.toImmutableMap()

            val todoTags = todoRepository.loadTags()

            setState {
                copy(
                    todoInfoMap = todoInfoMap,
                    todoScheduleMap = todoScheduleMap,
                    tags = todoTags.toUiModels(),
                )
            }
        }
    }

    override suspend fun processIntent(intent: ScheduleIntent) {
        when (intent) {
            is ScheduleIntent.OnTagClick -> onTagClick(intent.tag)
            is ScheduleIntent.OnInfoClick -> onInfoClick(intent.todoInfo)
            is ScheduleIntent.OnScheduleClick -> onScheduleClick(intent.schedule)
        }
    }

    private fun onTagClick(tag: TodoTagUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Tag")
        )
        setSelectedTag(tag)
    }

    private fun onInfoClick(todoInfo: TodoInfoUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Info")
        )
        setSelectedTodoInfo(todoInfo)
    }

    private suspend fun onScheduleClick(schedule: TodoScheduleUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Schedule")
        )
        onCheckedChange(schedule)
    }

    private fun setSelectedTag(tag: TodoTagUiModel) {
        if (tag == currentState.selectedTag) return

        setState {
            copy(
                selectedTag = tag,
                selectedTodoInfo = null,
            )
        }
    }

    private fun setSelectedTodoInfo(todoInfo: TodoInfoUiModel) {
        if (todoInfo == currentState.selectedTodoInfo) return

        setState { copy(selectedTodoInfo = todoInfo) }
    }

    companion object {
        private const val SCREEN_NAME = "Schedule"
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
