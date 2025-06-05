package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.RepeatCycle.Companion.DISPLAY_ERROR
import com.tgyuu.repeatcycle.util.parsingIntervals
import com.tgyuu.repeatcycle.util.toPreviewIntervals
import java.time.DayOfWeek

data class AddRepeatCycleState(
    val intervals: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val previewRepeatCycle = parsingIntervals(intervals)
        .getOrDefault(emptyList())
        .toPreviewIntervals()

    val isSaveEnabled = intervals.isNotEmpty() && previewRepeatCycle != DISPLAY_ERROR
}
