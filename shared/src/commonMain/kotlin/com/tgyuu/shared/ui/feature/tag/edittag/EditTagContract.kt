package com.tgyuu.shared.ui.feature.tag.edittag

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.TodoTag

@Immutable
data class EditTagState(
    val originTag: TodoTag? = null,
    val name: String = "",
    val colorValue: Int = DEFAULT_TAG_COLOR,
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled: Boolean
        get() = name.isNotBlank() && (name != originTag?.name || colorValue != originTag.color)

    companion object {
        // Android EditTagState와 동일: 기본 색상은 DefaultTodoTag.color(연한 파랑)
        val DEFAULT_TAG_COLOR = DefaultTodoTag.color
    }
}

sealed class EditTagIntent : UiIntent {
    data object OnBackClick : EditTagIntent()
    data class OnNameChange(val name: String) : EditTagIntent()
    data class OnColorChange(val color: Int) : EditTagIntent()
    data object OnUpdateClick : EditTagIntent()
}
