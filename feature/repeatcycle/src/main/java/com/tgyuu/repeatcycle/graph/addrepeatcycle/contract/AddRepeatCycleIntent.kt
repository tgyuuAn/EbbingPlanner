package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiIntent
import kotlinx.datetime.DayOfWeek

sealed interface AddRepeatCycleIntent : UiIntent {
    data object OnBackClick : AddRepeatCycleIntent
    data class OnRepeatCycleChange(val repeatCycle: String) : AddRepeatCycleIntent
    data object OnSaveClick : AddRepeatCycleIntent
}
