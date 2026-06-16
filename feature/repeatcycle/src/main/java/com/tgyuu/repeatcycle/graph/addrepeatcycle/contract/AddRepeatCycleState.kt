package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.model.Experiment.SaveButtonPosition
import com.tgyuu.repeatcycle.util.parsingIntervals
import com.tgyuu.repeatcycle.util.toPreviewIntervals
import java.time.DayOfWeek

data class AddRepeatCycleState(
    val intervals: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
    val saveButtonPositionVariant: SaveButtonPosition.Variant = SaveButtonPosition.Variant.CONTROL,
    val resourceProvider: ResourceProvider? = null,
) : UiState {
    val isTreatment = saveButtonPositionVariant == Experiment.SaveButtonPosition.Variant.TREATMENT
    val previewRepeatCycle = resourceProvider?.let {
        parsingIntervals(intervals)
            .getOrDefault(emptyList())
            .toPreviewIntervals(it)
    } ?: ""

    val isSaveEnabled =
        intervals.isNotEmpty() && parsingIntervals(intervals).getOrNull()?.isNotEmpty() == true
}
