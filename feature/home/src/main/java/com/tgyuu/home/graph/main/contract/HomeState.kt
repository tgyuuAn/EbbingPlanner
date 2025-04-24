package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

data class HomeState(
    val isLoading: Boolean = true,
    val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> = emptyMap(),
    val schedulesByTodoInfo: Map<Int, List<TodoSchedule>> = emptyMap(),
    val sortType: SortType = SortType.CREATED,
) : UiState

enum class SortType(val displayName: String) {
    CREATED("생성순"),
    NAME("이름순"),
    PRIORITY("우선순");
}
