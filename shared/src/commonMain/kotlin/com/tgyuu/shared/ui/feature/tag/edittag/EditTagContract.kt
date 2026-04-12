package com.tgyuu.shared.ui.feature.tag.edittag

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.TodoTag

@Immutable
data class EditTagState(
    val originTag: TodoTag? = null,
    val name: String = "",
    val colorValue: Int = DEFAULT_TAG_COLOR,
) : UiState {
    val isSaveEnabled: Boolean
        get() = name.isNotBlank() && (name != originTag?.name || colorValue != originTag.color)

    companion object {
        const val DEFAULT_TAG_COLOR = 0xFFFF6B6B.toInt()
    }
}

sealed class EditTagIntent : UiIntent {
    data object OnBackClick : EditTagIntent()
    data class OnNameChange(val name: String) : EditTagIntent()
    data object OnColorDropDownClick : EditTagIntent()
    data class OnColorChange(val color: Int) : EditTagIntent()
    data object OnUpdateClick : EditTagIntent()
}
