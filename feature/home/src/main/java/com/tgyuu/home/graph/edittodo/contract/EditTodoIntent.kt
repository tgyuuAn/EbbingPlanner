package com.tgyuu.home.graph.edittodo.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

sealed class EditTodoIntent : UiIntent {
    data object OnBackClick : EditTodoIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : EditTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : EditTodoIntent()
    data class OnTitleChange(val title: String) : EditTodoIntent()
    data class OnPriorityChange(val priority: String) : EditTodoIntent()
    data class OnTagDropDownClick(val content: BottomSheetContent) : EditTodoIntent()
    data class OnTagChange(val tag: TodoTag) : EditTodoIntent()
    data object OnAddTagClick : EditTodoIntent()
    data class OnRepeatCycleDropDownClick(val content: BottomSheetContent) : EditTodoIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycle) : EditTodoIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : EditTodoIntent()
    data object OnSaveClick : EditTodoIntent()
}
