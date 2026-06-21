package com.tgyuu.shared.ui.feature.tag.edittag

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_tag_load_failed
import ebbingplanner.shared.generated.resources.snack_tag_name_exists
import ebbingplanner.shared.generated.resources.snack_tag_update_failed
import ebbingplanner.shared.generated.resources.snack_tag_updated
import org.jetbrains.compose.resources.getString

class EditTagViewModel(
    private val tagId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val onShowColorBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditTagState, EditTagIntent>(EditTagState()) {

    init {
        loadExperimentVariant()
        loadTag()
    }

    private fun loadTag() {
        safeScope.launch {
            try {
                val tag = todoRepository.loadTag(tagId) ?: run {
                    onNavigateBack()
                    return@launch
                }
                setState {
                    copy(
                        originTag = tag,
                        name = tag.name,
                        colorValue = tag.color,
                    )
                }
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_tag_load_failed))
            }
        }
    }

    override suspend fun processIntent(intent: EditTagIntent) {
        when (intent) {
            EditTagIntent.OnBackClick -> onNavigateBack()
            is EditTagIntent.OnNameChange -> onNameChange(intent.name)
            EditTagIntent.OnColorDropDownClick -> onShowColorBottomSheet?.invoke()
            is EditTagIntent.OnColorChange -> onColorChange(intent.color)
            EditTagIntent.OnUpdateClick -> onUpdateClick()
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

    private suspend fun onUpdateClick() {
        val originTag = currentState.originTag ?: return
        if (!currentState.isSaveEnabled) return

        try {
            val trimmedName = currentState.name.trim()
            val existingTags = todoRepository.loadTags()
            val isDuplicate = existingTags.any {
                it.id != originTag.id && it.name.equals(trimmedName, ignoreCase = true)
            }
            if (isDuplicate) {
                onShowSnackbar(getString(Res.string.snack_tag_name_exists))
                return
            }

            val updatedTag = TodoTag(
                id = originTag.id,
                name = trimmedName,
                color = currentState.colorValue,
                createdAt = originTag.createdAt,
            )
            todoRepository.updateTag(updatedTag)
            onShowSnackbar(getString(Res.string.snack_tag_updated))
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_tag_update_failed))
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
