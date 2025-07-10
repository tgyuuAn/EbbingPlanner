package com.tgyuu.setting.graph.theme.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.Theme

sealed interface ThemeIntent : UiIntent {
    data object OnBackClick : ThemeIntent
    data class OnThemeChange(val theme: Theme) : ThemeIntent
    data object OnUpdateClick : ThemeIntent
}
