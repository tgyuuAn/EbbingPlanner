package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

data class HomeState(
    val isLoading: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> = emptyMap(),
) : UiState
