package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent
    data class OnCheckedChange(val todoSchedule: TodoSchedule) : HomeIntent
}
