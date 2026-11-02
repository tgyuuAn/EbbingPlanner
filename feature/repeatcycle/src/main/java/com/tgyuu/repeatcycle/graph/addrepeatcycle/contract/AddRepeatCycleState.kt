package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.repeatcycle.util.parsingIntervals
import com.tgyuu.repeatcycle.util.toPreviewIntervals
import kotlinx.datetime.DayOfWeek

data class AddRepeatCycleState(
    val intervals: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
    val resourceProvider: ResourceProvider? = null,
) : UiState {
    val previewRepeatCycle = resourceProvider?.let {
        parsingIntervals(intervals)
            .getOrDefault(emptyList())
            .toPreviewIntervals(it)
    } ?: ""

    val isSaveEnabled =
        intervals.isNotEmpty() && parsingIntervals(intervals).getOrNull()?.isNotEmpty() == true
}
