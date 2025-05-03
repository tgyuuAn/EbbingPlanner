package com.tgyuu.tag.graph.edittag

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.InputState.Companion.getStringInputState
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.tag.graph.edittag.contract.EditTagIntent
import com.tgyuu.tag.graph.edittag.contract.EditTagState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditTagViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<EditTagState, EditTagIntent>(EditTagState()) {

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
        val newState = currentState.copy(nameInputState = getStringInputState(currentState.name))

        if (newState.isInputFieldIncomplete) {
            setState { newState }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        todoRepository.addTag(
            name = currentState.name.trim(),
            color = currentState.colorValue,
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("새로운 태그를 추가하였습니다"))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
