package com.tgyuu.dashboard

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
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
                    )
                }.distinctBy { it.id }
            }

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
                    )
                }.distinctBy { it.id }
            }

            val todoTags = todoInfoMap.keys.map { tagId ->
                todoRepository.loadTag(tagId)
            }

            setState {
                copy(
                    todoInfoMap = todoInfoMap,
                    todoScheduleMap = todoScheduleMap,
                    tags = todoTags,
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

    private fun setSelectedTag(tag: TodoTag) {
        if (tag == currentState.selectedTag) return

        setState {
            copy(
                selectedTag = tag,
                selectedTodoInfo = null,
            )
        }
    }

    private fun setSelectedTodoInfo(todoInfo: TodoInfo) {
        if (todoInfo == currentState.selectedTodoInfo) return

        setState { copy(selectedTodoInfo = todoInfo) }
    }

    private suspend fun onCheckedChange(schedule: TodoSchedule) {
        val newSchedule = schedule.copy(isDone = !schedule.isDone)
        todoRepository.updateTodo(newSchedule)

        val updatedMap = currentState.todoScheduleMap.toMutableMap()
        val schedules = updatedMap[schedule.infoId].orEmpty().map {
            if (it.id == schedule.id) newSchedule else it
        }
        updatedMap[schedule.infoId] = schedules
        setState { copy(todoScheduleMap = updatedMap) }
    }
}
