package com.tgyuu.repeatcycle.graph.editrepeatcycle.contract

import com.tgyuu.common.base.UiIntent
import java.time.DayOfWeek

sealed interface EditRepeatCycleIntent : UiIntent {
    data object OnBackClick : EditRepeatCycleIntent
    data class OnRepeatCycleChange(val memo: String) : EditRepeatCycleIntent
    data class OnRestDayChange(val restDay: DayOfWeek) : EditRepeatCycleIntent
    data object OnUpdateClick : EditRepeatCycleIntent
}
