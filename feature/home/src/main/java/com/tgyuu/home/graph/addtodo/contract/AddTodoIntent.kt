package com.tgyuu.home.graph.addtodo.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

sealed class AddTodoIntent : UiIntent {
    data object OnBackClick : AddTodoIntent()
    data class OnSelectedDataChangeClick(val content: BottomSheetContent) : AddTodoIntent()
    data class OnSelectedDateChange(val selectedDate: LocalDate) : AddTodoIntent()
    data class OnTitleChange(val title: String) : AddTodoIntent()
    data class OnPinnedChange(val isPinned: Boolean) : AddTodoIntent()
    data class OnTagDropDownClick(val content: BottomSheetContent) : AddTodoIntent()
    data class OnTagChange(val tag: TodoTagUiModel) : AddTodoIntent()
    data object OnAddTagClick : AddTodoIntent()
    data object OnAddRepeatCycleClick : AddTodoIntent()
    data class OnRepeatCycleDropDownClick(val content: BottomSheetContent) : AddTodoIntent()
    data class OnRepeatCycleChange(val repeatCycle: RepeatCycleUiModel) : AddTodoIntent()
    data class OnRestDayChange(val restDay: DayOfWeek) : AddTodoIntent()
    data object OnSaveClick : AddTodoIntent()

    // Notification 관련 Intent
    data object OnNotificationToggleClick : AddTodoIntent()
    data class OnAlarmTimeChange(val hour: Int, val minute: Int) : AddTodoIntent()
    data class OnAlarmMessageChange(val message: String) : AddTodoIntent()
    data object OnAlarmMessageReset : AddTodoIntent()
    data object OnNotificationBackClick : AddTodoIntent()
    data object OnNotificationSaveClick : AddTodoIntent()
}
