package com.tgyuu.home.graph.edittodo

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.InputState.Companion.getStringInputState
import com.tgyuu.home.graph.edittodo.contract.EditTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoState
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)
            val originTag = todoRepository.loadTag(originSchedule.tagId)

            setState {
                copy(
                    originSchedule = originSchedule,
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    priority = originSchedule.priority.takeIf { it != 0 }?.toString() ?: "",
                    tag = originTag,
                )
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTagList()
        setState { copy(tagList = loadedTagList) }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is EditTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            is EditTodoIntent.OnTagDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnTagChange -> onTagChange(intent.tag)
            EditTodoIntent.OnAddTagClick -> onAddTagClick()
            EditTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onPriorityChange(priority: String) {
        if (!priority.isDigitsOnly()) return
        if (priority.length >= 4) return

        setState { copy(priority = priority) }
    }

    private suspend fun onTagChange(todoTag: TodoTag) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
    }

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(HomeGraph.AddTagRoute))
    }

    private suspend fun onSaveClick() {
        val newState = currentState.copy(
            titleInputState = getStringInputState(currentState.title.trim())
        )

        if (newState.isInputFieldIncomplete) {
            setState { newState }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        val newSchedule = newState.originSchedule!!.copy(
            title = newState.title,
            tagId = newState.tag.id,
            name = newState.tag.name,
            color = newState.tag.color,
            priority = newState.priority?.toIntOrNull() ?: 0,
        )

        todoRepository.updateTodo(newSchedule)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("일정을 업데이트 하였습니다"))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
