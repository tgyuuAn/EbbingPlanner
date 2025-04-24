package com.tgyuu.home.graph.addtag

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.InputState.Companion.getStringInputState
import com.tgyuu.home.graph.addtag.contract.AddTagIntent
import com.tgyuu.home.graph.addtag.contract.AddTagState
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTagViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<AddTagState, AddTagIntent>(AddTagState()) {

    override suspend fun processIntent(intent: AddTagIntent) {
        when (intent) {
            AddTagIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is AddTagIntent.OnNameChange -> onNameChange(intent.name)
            is AddTagIntent.OnColorDropDownClick -> eventBus.sendEvent(
                EbbingEvent.ShowBottomSheet(intent.content)
            )

            is AddTagIntent.OnColorChange -> onColorChange(intent.colorValue)

            AddTagIntent.OnSaveClick -> onSaveClick()
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
