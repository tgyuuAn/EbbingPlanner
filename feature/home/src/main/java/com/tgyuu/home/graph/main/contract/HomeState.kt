package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.SortType
import java.time.LocalDate

data class HomeState(
    val isLoading: Boolean = true,
    val schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>> = emptyMap(),
    val schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>> = emptyMap(),
    val sortType: SortType = SortType.CREATED,
    val showWidgetNudgeDialog: Boolean = false,
) : UiState
