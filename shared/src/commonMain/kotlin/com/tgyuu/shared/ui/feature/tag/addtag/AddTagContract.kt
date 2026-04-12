package com.tgyuu.shared.ui.feature.tag.addtag

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

@Immutable
data class AddTagState(
    val name: String = "",
    val colorValue: Int = DEFAULT_TAG_COLOR,
) : UiState {
    val isSaveEnabled: Boolean
        get() = name.isNotBlank()

    companion object {
        const val DEFAULT_TAG_COLOR = 0xFFFF6B6B.toInt()
    }
}

sealed class AddTagIntent : UiIntent {
    data object OnBackClick : AddTagIntent()
    data class OnNameChange(val name: String) : AddTagIntent()
    data object OnColorDropDownClick : AddTagIntent()
    data class OnColorChange(val color: Int) : AddTagIntent()
    data object OnSaveClick : AddTagIntent()
}
