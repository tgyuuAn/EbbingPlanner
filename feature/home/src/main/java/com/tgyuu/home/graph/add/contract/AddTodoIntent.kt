package com.tgyuu.home.graph.add.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.RepeatCycle
import com.tgyuu.domain.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

sealed class AddTodoIntent : UiIntent {
    data object OnBackClick : AddTodoIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : AddTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : AddTodoIntent()
    data class OnTitleChange(val title: String) : AddTodoIntent()
    data class OnTagChange(val tag: TodoTag) : AddTodoIntent()
    data class OnRepeatCycleDropDownClick(val content: BottomSheetContent) : AddTodoIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycle) : AddTodoIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : AddTodoIntent()
    data object OnSaveClick : AddTodoIntent()
}
