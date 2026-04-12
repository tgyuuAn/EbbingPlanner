package com.tgyuu.shared.ui.feature.schedule

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
) : BaseViewModel<ScheduleState, ScheduleIntent>(ScheduleState()) {

    private var isLoaded = false

    fun loadTodoSchedules() {
        if (isLoaded) return

        safeScope.launch {
            try {
                setState { copy(isLoading = true) }

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
                        tags = todoTags.map { it.toUiModel() }.toImmutableList(),
                        isLoading = false,
                    )
                }
                isLoaded = true
            } catch (e: Exception) {
                println("loadTodoSchedules error: ${e.message}")
                setState { copy(isLoading = false) }
            }
        }
    }

    override suspend fun processIntent(intent: ScheduleIntent) {
        when (intent) {
            ScheduleIntent.OnBackClick -> onNavigateBack()
            is ScheduleIntent.OnTagClick -> setSelectedTag(intent.tag)
            is ScheduleIntent.OnInfoClick -> setSelectedTodoInfo(intent.todoInfo)
            is ScheduleIntent.OnScheduleClick -> onCheckedChange(intent.schedule)
        }
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

    private fun TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun TodoScheduleUiModel.toDomainModel() = TodoSchedule(
        id = id,
        infoId = infoId,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        date = date,
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )
}
