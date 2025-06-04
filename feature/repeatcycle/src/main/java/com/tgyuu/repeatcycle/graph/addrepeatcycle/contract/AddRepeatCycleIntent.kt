package com.tgyuu.repeatcycle.graph.addrepeatcycle.contract

import com.tgyuu.common.base.UiIntent
import java.time.DayOfWeek

sealed interface AddRepeatCycleIntent : UiIntent {
    data object OnBackClick : AddRepeatCycleIntent
    data class OnRepeatCycleChange(val repeatCycle: String) : AddRepeatCycleIntent
    data class OnRestDayChange(val restDay: DayOfWeek) : AddRepeatCycleIntent
    data object OnSaveClick : AddRepeatCycleIntent
}
