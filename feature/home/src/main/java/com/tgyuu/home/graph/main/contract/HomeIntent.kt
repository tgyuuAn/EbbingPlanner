package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent
}
