package com.tgyuu.setting.graph.theme.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Theme
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition

data class ThemeState(
    val originTheme: Theme? = null,
    val selectTheme: Theme? = null,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled = originTheme != selectTheme
}
