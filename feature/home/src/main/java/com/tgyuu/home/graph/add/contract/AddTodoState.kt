package com.tgyuu.home.graph.add.contract

import com.tgyuu.common.base.UiState
import java.time.LocalDate

data class AddTodoState(
    val selectedDate: LocalDate = LocalDate.now(),
) : UiState
