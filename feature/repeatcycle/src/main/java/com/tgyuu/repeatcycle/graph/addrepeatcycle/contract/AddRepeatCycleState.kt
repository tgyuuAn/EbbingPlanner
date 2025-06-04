package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import java.time.DayOfWeek

data class AddRepeatCycleState(
    val repeatCycle: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = repeatCycle.isNotEmpty()
    val repeatCycleInputState: InputState = InputState.getStringInputState(repeatCycle)
}
