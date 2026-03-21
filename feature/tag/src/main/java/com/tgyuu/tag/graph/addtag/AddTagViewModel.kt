package com.tgyuu.tag.graph.addtag

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.tag.graph.addtag.contract.AddTagIntent
import com.tgyuu.tag.graph.addtag.contract.AddTagState
class AddTagViewModel(
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
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_required_fields)))
            return
        }

        todoRepository.addTag(
            name = currentState.name.trim(),
            color = currentState.colorValue,
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_added)))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
