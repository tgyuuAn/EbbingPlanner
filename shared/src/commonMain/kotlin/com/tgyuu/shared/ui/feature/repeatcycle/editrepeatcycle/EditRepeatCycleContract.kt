package com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.parsingIntervals
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.toPreviewIntervals

data class EditRepeatCycleState(
    val originRepeatCycle: RepeatCycle? = null,
    val intervals: String = "",
) : UiState {
    val previewRepeatCycle: String = parsingIntervals(intervals)
        .getOrDefault(emptyList())
        .toPreviewIntervals()

    val isSaveEnabled: Boolean = intervals.isNotEmpty() && previewRepeatCycle != RepeatCycle.DISPLAY_ERROR
}

sealed interface EditRepeatCycleIntent : UiIntent {
    data object OnBackClick : EditRepeatCycleIntent
    data class OnIntervalsChange(val intervals: String) : EditRepeatCycleIntent
    data object OnUpdateClick : EditRepeatCycleIntent
}
