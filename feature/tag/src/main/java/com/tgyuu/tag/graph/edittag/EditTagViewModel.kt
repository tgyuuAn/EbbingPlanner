package com.tgyuu.tag.graph.edittag

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.tag.graph.edittag.contract.EditTagIntent
import com.tgyuu.tag.graph.edittag.contract.EditTagState
import kotlinx.coroutines.launch

class EditTagViewModel(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditTagState, EditTagIntent>(EditTagState()) {

    init {
        val tagId = savedStateHandle.get<Int>("tagId")
            ?: throw IllegalArgumentException("해당 태그는 없습니다")

        viewModelScope.launch {
            val originTag = todoRepository.loadTag(tagId) ?: run {
                navigationBus.navigate(NavigationEvent.Up)
                return@launch
            }

            setState {
                copy(
                    originTag = originTag,
                    name = originTag.name,
                    colorValue = originTag.color,
                )
            }
        }
    }

    override suspend fun processIntent(intent: EditTagIntent) {
        when (intent) {
            EditTagIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is EditTagIntent.OnNameChange -> onNameChange(intent.name)
            is EditTagIntent.OnColorDropDownClick -> eventBus.sendEvent(
                EbbingEvent.ShowBottomSheet(intent.content)
            )

            is EditTagIntent.OnColorChange -> onColorChange(intent.colorValue)

            EditTagIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onNameChange(name: String) {
        setState { copy(name = name) }
    }

    private suspend fun onColorChange(colorValue: Int) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(colorValue = colorValue) }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        val originTag = currentState.originTag ?: return
        val trimmedName = currentState.name.trim()
        val existingTags = todoRepository.loadTags()
        if (existingTags.any { it.id != originTag.id && it.name.equals(trimmedName, ignoreCase = true) }) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("이미 존재하는 태그 이름입니다"))
            return
        }

        todoRepository.updateTag(
            todoTag = originTag.copy(
                name = trimmedName,
                color = currentState.colorValue,
            )
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("태그를 수정하였습니다"))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
