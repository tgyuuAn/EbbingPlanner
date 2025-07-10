package com.tgyuu.setting.graph.theme.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Theme

data class ThemeState(
    val originTheme: Theme? = null,
    val selectTheme: Theme? = null,
) : UiState {
    val isSaveEnabled = originTheme != selectTheme
}
