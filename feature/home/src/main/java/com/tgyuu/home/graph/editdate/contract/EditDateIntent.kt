package com.tgyuu.home.graph.editdate.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import java.time.DayOfWeek
import java.time.LocalDate

sealed class EditDateIntent : UiIntent {
    data object OnBackClick : EditDateIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : EditDateIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditDateIntent()
    data object OnAddRepeatCycleClick : EditDateIntent()
    data class OnRepeatCycleDropDownClick(val content: BottomSheetContent) : EditDateIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycleUiModel) : EditDateIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : EditDateIntent()
    data class OnSaveClick(val isDoneSchedule: List<Boolean>) : EditDateIntent()
}
