package com.tgyuu.shared.ui.feature.setting.theme

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.model.Theme

data class ThemeState(
    val originTheme: Theme? = null,
    val selectTheme: Theme? = null,
    val saveButtonPositionVariant: Experiment.SaveButtonPosition.Variant = Experiment.SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isSaveEnabled = originTheme != selectTheme
    val isTreatment: Boolean = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
}

sealed interface ThemeIntent : UiIntent {
    data object OnBackClick : ThemeIntent
    data class OnThemeChange(val theme: Theme) : ThemeIntent
    data object OnUpdateClick : ThemeIntent
}
