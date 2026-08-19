package com.tgyuu.shared.ui.feature.tag.addtag

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import kotlinx.coroutines.launch
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_tag_add_failed
import ebbingplanner.shared.generated.resources.snack_tag_added
import ebbingplanner.shared.generated.resources.snack_tag_name_exists
import org.jetbrains.compose.resources.getString

class AddTagViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val onShowColorBottomSheet: (() -> Unit)? = null,
    private val analyticsHelper: AnalyticsHelper? = null,
) : BaseViewModel<AddTagState, AddTagIntent>(AddTagState()) {

    init {
        loadExperimentVariant()
    }

    override suspend fun processIntent(intent: AddTagIntent) {
        when (intent) {
            AddTagIntent.OnBackClick -> onNavigateBack()
            is AddTagIntent.OnNameChange -> onNameChange(intent.name)
            AddTagIntent.OnColorDropDownClick -> onShowColorBottomSheet?.invoke()
            is AddTagIntent.OnColorChange -> onColorChange(intent.color)
            AddTagIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onNameChange(name: String) {
        if (name.length <= 20) {
            setState { copy(name = name) }
        }
    }

    private fun onColorChange(color: Int) {
        setState { copy(colorValue = color) }
    }

    private suspend fun onSaveClick() {
        analyticsHelper.logClick("AddTag", "Save")
        if (!currentState.isSaveEnabled) return

        try {
            val existingTags = todoRepository.loadTags()
            if (existingTags.any { it.name.equals(currentState.name.trim(), ignoreCase = true) }) {
                onShowSnackbar(getString(Res.string.snack_tag_name_exists))
                return
            }

            val newId = todoRepository.addTag(
                name = currentState.name.trim(),
                color = currentState.colorValue,
            )
            if (newId <= 0L) {
                onShowSnackbar(getString(Res.string.snack_tag_add_failed))
                return
            }
            onShowSnackbar(getString(Res.string.snack_tag_added))
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_tag_add_failed))
        }
    }

    private fun loadExperimentVariant() {
        safeScope.launch {
            val variant = experimentRepository?.getVariant(Experiment.SaveButtonPosition)
                ?: Experiment.SaveButtonPosition.Variant.CONTROL
            setState { copy(saveButtonPositionVariant = variant) }
        }
    }
}
