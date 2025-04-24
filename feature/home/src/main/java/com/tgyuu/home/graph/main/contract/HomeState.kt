package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

data class HomeState(
    val isLoading: Boolean = true,
    val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> = emptyMap(),
    val schedulesByTodoInfo: Map<Int, List<TodoSchedule>> = emptyMap(),
    val sortType: SortType = SortType.CREATED,
) : UiState
