package com.tgyuu.tag.graph.addtag

import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
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
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<AddTagState, AddTagIntent>(AddTagState()) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "AddTag",
            )
        )
    }

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
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "AddTag",
                buttonName = "Save",
            )
        )

        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_required_fields)))
            return
        }

        val existingTags = todoRepository.loadTags()
        if (existingTags.any { it.name.equals(currentState.name.trim(), ignoreCase = true) }) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("이미 존재하는 태그 이름입니다"))
            return
        }

        val newId = todoRepository.addTag(
            name = currentState.name.trim(),
            color = currentState.colorValue,
        )
        if (newId <= 0L) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("태그 추가에 실패했습니다"))
            return
        }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_added)))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
