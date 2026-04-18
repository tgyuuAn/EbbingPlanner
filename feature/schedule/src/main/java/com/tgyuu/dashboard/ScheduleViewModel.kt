package com.tgyuu.dashboard

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
import com.tgyuu.domain.repository.TodoRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
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
        setSelectedTag(tag)
    }

    private fun onInfoClick(todoInfo: TodoInfoUiModel) {
        setSelectedTodoInfo(todoInfo)
    }

    private suspend fun onScheduleClick(schedule: TodoScheduleUiModel) {
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
