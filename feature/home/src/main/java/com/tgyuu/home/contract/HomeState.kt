package com.tgyuu.home.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.home.model.TodoRO
import java.time.LocalDate

data class HomeState(
    val todosByDateMap: Map<LocalDate, List<TodoRO>> = emptyMap(),
) : UiState
