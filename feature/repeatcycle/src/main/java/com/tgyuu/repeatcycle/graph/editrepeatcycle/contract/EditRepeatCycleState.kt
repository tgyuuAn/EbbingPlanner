package com.tgyuu.repeatcycle.graph.editrepeatcycle.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.TodoSchedule
import java.time.DayOfWeek

data class EditRepeatCycleState(
    val repeatCycle: String = "",
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = repeatCycle.isNotEmpty()
    val repeatCycleInputState: InputState = InputState.getStringInputState(repeatCycle)
}
