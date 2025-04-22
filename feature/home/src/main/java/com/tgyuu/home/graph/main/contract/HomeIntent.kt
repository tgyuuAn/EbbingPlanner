package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed interface HomeIntent : UiIntent {
    data object OnAddTodoClick : HomeIntent
}
