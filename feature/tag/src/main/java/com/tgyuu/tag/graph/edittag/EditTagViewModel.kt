package com.tgyuu.tag.graph.edittag

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.tag.graph.edittag.contract.EditTagIntent
import com.tgyuu.tag.graph.edittag.contract.EditTagState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditTagViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val experimentRepository: ExperimentRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<EditTagState, EditTagIntent>(EditTagState(saveButtonPositionVariant = runBlocking { experimentRepository.getVariant(Experiment.SaveButtonPosition) })) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "EditTag",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2"),
            )
        )

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
            EditTagIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "EditTag", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
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
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "EditTag",
                buttonName = "Save",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2")
            )
        )

        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_required_fields)))
            return
        }

        todoRepository.updateTag(
            todoTag = currentState.originTag!!.copy(
                name = currentState.name,
                color = currentState.colorValue,
            )
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_updated)))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
