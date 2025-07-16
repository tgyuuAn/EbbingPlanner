package com.tgyuu.setting.graph.widget.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.Theme

sealed interface WidgetIntent : UiIntent {
    data object OnBackClick : WidgetIntent
    data class OnThemeChange(val theme: Theme) : WidgetIntent
    data class OnAlphaChange(val alpha: Float) : WidgetIntent
    data object OnSaveClick : WidgetIntent
}
