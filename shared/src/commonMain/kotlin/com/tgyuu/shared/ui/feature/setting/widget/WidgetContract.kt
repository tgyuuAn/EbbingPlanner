package com.tgyuu.shared.ui.feature.setting.widget

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Theme

data class WidgetState(
    val originTheme: Theme? = null,
    val selectedTheme: Theme? = null,
    val originBackgroundAlpha: Float? = null,
    val selectedBackgroundAlpha: Float? = null,
    val originTextAlpha: Float? = null,
    val selectedTextAlpha: Float? = null,
) : UiState {
    val isSaveEnabled = (originTheme != selectedTheme) ||
            (originBackgroundAlpha != selectedBackgroundAlpha) ||
            (originTextAlpha != selectedTextAlpha)
}

sealed interface WidgetIntent : UiIntent {
    data object OnBackClick : WidgetIntent
    data class OnThemeChange(val theme: Theme) : WidgetIntent
    data class OnBackgroundAlphaChange(val alpha: Float) : WidgetIntent
    data class OnTextAlphaChange(val alpha: Float) : WidgetIntent
    data object OnSaveClick : WidgetIntent
}
