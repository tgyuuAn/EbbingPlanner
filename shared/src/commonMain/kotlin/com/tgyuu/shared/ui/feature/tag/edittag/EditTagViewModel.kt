package com.tgyuu.shared.ui.feature.tag.edittag

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch

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
                onShowSnackbar("태그를 불러오는데 실패했습니다")
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
            val updatedTag = TodoTag(
                id = originTag.id,
                name = currentState.name,
                color = currentState.colorValue,
                createdAt = originTag.createdAt,
            )
            todoRepository.updateTag(updatedTag)
            onShowSnackbar("태그를 수정하였습니다")
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar("태그 수정에 실패했습니다")
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
