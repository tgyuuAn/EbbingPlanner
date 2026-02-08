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
            is ScheduleIntent.OnTagClick -> setSelectedTag(intent.tag)
            is ScheduleIntent.OnInfoClick -> setSelectedTodoInfo(intent.todoInfo)
            is ScheduleIntent.OnScheduleClick -> onCheckedChange(intent.schedule)
        }
    }

    private fun setSelectedTag(tag: TodoTagUiModel) {
        if (tag == currentState.selectedTag) return

        analyticsHelper.logEvent(AnalyticsEvent(type = ScheduleAnalytics.TAG_CLICK))

        setState {
            copy(
                selectedTag = tag,
                selectedTodoInfo = null,
            )
        }
    }

    private fun setSelectedTodoInfo(todoInfo: TodoInfoUiModel) {
        if (todoInfo == currentState.selectedTodoInfo) return

        analyticsHelper.logEvent(AnalyticsEvent(type = ScheduleAnalytics.INFO_CLICK))

        setState { copy(selectedTodoInfo = todoInfo) }
    }

    private suspend fun onCheckedChange(schedule: TodoScheduleUiModel) {
        val domainSchedule = schedule.toDomainModel()
        val newDomainSchedule = domainSchedule.copy(isDone = !domainSchedule.isDone)
        todoRepository.updateTodo(newDomainSchedule)

        analyticsHelper.logEvent(AnalyticsEvent(type = ScheduleAnalytics.SCHEDULE_TOGGLE))

        val newSchedule = newDomainSchedule.toUiModel()
        val updatedMap = currentState.todoScheduleMap.toMutableMap()
        val schedules = updatedMap[schedule.infoId].orEmpty().map {
            if (it.id == schedule.id) newSchedule else it
        }.toImmutableList()
        updatedMap[schedule.infoId] = schedules
        setState { copy(todoScheduleMap = updatedMap.toImmutableMap()) }
    }
}
