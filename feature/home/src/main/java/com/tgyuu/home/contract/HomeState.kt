package com.tgyuu.home.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.Todo
import java.time.LocalDate

data class HomeState(
    val todosByDateMap: Map<LocalDate, List<Todo>> = emptyMap(),
) : UiState
