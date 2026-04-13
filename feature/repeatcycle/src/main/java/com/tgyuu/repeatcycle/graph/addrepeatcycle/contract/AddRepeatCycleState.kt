package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.RepeatCycle.Companion.DISPLAY_ERROR
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition
import com.tgyuu.repeatcycle.util.parsingIntervals
import com.tgyuu.repeatcycle.util.toPreviewIntervals
import java.time.DayOfWeek

data class AddRepeatCycleState(
    val intervals: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val previewRepeatCycle = parsingIntervals(intervals)
        .getOrDefault(emptyList())
        .toPreviewIntervals()

    val isSaveEnabled = intervals.isNotEmpty() && previewRepeatCycle != DISPLAY_ERROR
}
