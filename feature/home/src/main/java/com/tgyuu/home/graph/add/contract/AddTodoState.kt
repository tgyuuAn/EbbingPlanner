package com.tgyuu.home.graph.add.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.RepeatCycle
import com.tgyuu.domain.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

data class AddTodoState(
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val tag: TodoTag? = null,
    val repeatCycle: RepeatCycle = RepeatCycle.SAME_DAY,
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
}
