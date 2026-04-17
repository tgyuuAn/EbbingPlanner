package com.tgyuu.shared.ui.feature.tag.addtag

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import kotlinx.coroutines.launch
import com.tgyuu.shared.domain.repository.TodoRepository

class AddTagViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val onShowColorBottomSheet: (() -> Unit)? = null,
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
        if (!currentState.isSaveEnabled) return

        try {
            val existingTags = todoRepository.loadTags()
            if (existingTags.any { it.name.equals(currentState.name.trim(), ignoreCase = true) }) {
                onShowSnackbar("이미 존재하는 태그 이름입니다")
                return
            }

            val newId = todoRepository.addTag(
                name = currentState.name.trim(),
                color = currentState.colorValue,
            )
            if (newId <= 0L) {
                onShowSnackbar("태그 추가에 실패했습니다")
                return
            }
            onShowSnackbar("새로운 태그를 추가하였습니다")
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar("태그 추가에 실패했습니다")
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
