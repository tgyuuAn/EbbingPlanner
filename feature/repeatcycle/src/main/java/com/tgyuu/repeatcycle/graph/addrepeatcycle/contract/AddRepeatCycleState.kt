package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import java.time.DayOfWeek

data class AddRepeatCycleState(
    val repeatCycle: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = repeatCycle.isNotEmpty()
}
