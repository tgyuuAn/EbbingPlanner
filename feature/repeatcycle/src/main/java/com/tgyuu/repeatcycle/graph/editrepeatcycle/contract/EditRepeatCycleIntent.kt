package com.tgyuu.repeatcycle.graph.editrepeatcycle.contract

import com.tgyuu.common.base.UiIntent

sealed interface EditRepeatCycleIntent : UiIntent {
    data object OnBackClick : EditRepeatCycleIntent
    data class OnRepeatCycleChange(val repeatCycle: String) : EditRepeatCycleIntent
    data object OnUpdateClick : EditRepeatCycleIntent
}
