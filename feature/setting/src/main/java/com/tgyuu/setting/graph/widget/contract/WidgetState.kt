package com.tgyuu.setting.graph.widget.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Theme
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition

data class WidgetState(
    val originTheme: Theme? = null,
    val selectedTheme: Theme? = null,
    val originBackgroundAlpha: Float? = null,
    val selectedBackgroundAlpha: Float? = null,
    val originTextAlpha: Float? = null,
    val selectedTextAlpha: Float? = null,
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val isSaveEnabled = (originTheme != selectedTheme) || (originBackgroundAlpha != selectedBackgroundAlpha)
            || (originTextAlpha != selectedTextAlpha)
}
