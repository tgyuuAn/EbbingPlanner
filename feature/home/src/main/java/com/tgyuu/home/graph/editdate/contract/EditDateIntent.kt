package com.tgyuu.home.graph.editdate.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

sealed class EditDateIntent : UiIntent {
    data object OnBackClick : EditDateIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : EditDateIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditDateIntent()
    data object OnAddRepeatCycleClick : EditDateIntent()
    data class OnRepeatCycleDropDownClick(val content: BottomSheetContent) : EditDateIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycle) : EditDateIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : EditDateIntent()
    data class OnSaveClick(val isDoneSchedule: List<Boolean>) : EditDateIntent()
}
