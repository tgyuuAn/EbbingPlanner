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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    internal suspend fun loadTags() {
        suspendRunCatching {
            todoRepository.loadTags()
        }.onSuccess { tags ->
            setState { copy(tags = tags) }

            loadTodoInfos(tags)
        }
    }

    private suspend fun loadTodoInfos(tags: List<TodoTag>) = coroutineScope {
        suspendRunCatching {
            val infosPerTag: List<List<TodoInfo>> = tags.map { tag ->
                async { todoRepository.loadTodoInfosByTagId(tag.id) }
            }.awaitAll()

            tags.zip(infosPerTag).associate { (tag, infos) -> tag.id to infos }
        }.onSuccess {
            setState { copy(todoInfoMap = it) }

            loadTodoSchedules()
        }
    }

    private suspend fun loadTodoSchedules() = coroutineScope {
        suspendRunCatching {
            val todoInfos = currentState.todoInfoMap.values.flatten()

            val schedulesPerInfos: List<List<TodoSchedule>> = todoInfos.map {
                async { todoRepository.loadSchedulesByTodoInfo(it.id) }
            }.awaitAll()

            todoInfos.zip(schedulesPerInfos).associate { (todoInfo, schedules) ->
                todoInfo.id to schedules
            }
        }.onSuccess {
            setState { copy(todoScheduleMap = it) }
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
        setState {
            copy(
                selectedTag = tag,
                selectedTodoInfo = null,
            )
        }
    }

    private fun setSelectedTodoInfo(todoInfo: TodoInfo) {
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
