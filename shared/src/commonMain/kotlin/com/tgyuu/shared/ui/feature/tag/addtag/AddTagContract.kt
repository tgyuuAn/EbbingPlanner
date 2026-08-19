package com.tgyuu.shared.ui.feature.tag.addtag

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.model.Experiment

@Immutable
data class AddTagState(
    val name: String = "",
    val colorValue: Int = DEFAULT_TAG_COLOR,
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled: Boolean
        get() = name.isNotBlank()

    companion object {
        // Android AddTagState와 동일: 기본 색상은 DefaultTodoTag.color(연한 파랑)
        val DEFAULT_TAG_COLOR = DefaultTodoTag.color
    }
}

sealed class AddTagIntent : UiIntent {
    data object OnBackClick : AddTagIntent()
    data class OnNameChange(val name: String) : AddTagIntent()
    data object OnColorDropDownClick : AddTagIntent()
    data class OnColorChange(val color: Int) : AddTagIntent()
    data object OnSaveClick : AddTagIntent()
}
