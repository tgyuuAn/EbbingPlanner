package com.tgyuu.repeatcycle.graph.editrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.repeatcycle.util.parsingIntervals
import java.time.DayOfWeek

data class EditRepeatCycleState(
    val intervals: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val previewRepeatCycle = parsingIntervals(intervals).getOrDefault(emptyList())
    val isSaveEnabled = intervals.isNotEmpty()
}
