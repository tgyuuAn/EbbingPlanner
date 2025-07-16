package com.tgyuu.setting.graph.widget.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Theme

data class WidgetState(
    val originTheme: Theme? = null,
    val selectedTheme: Theme? = null,
    val originAlpha: Float? = null,
    val selectedAlpha: Float? = null,
) : UiState {
    val isSaveEnabled = (originTheme != selectedTheme) || (originAlpha != selectedAlpha)
}
