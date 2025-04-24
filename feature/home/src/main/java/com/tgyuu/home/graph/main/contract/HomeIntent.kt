package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent
    data class OnCheckedChange(val schedule: TodoSchedule) : HomeIntent
    data class OnEditScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnUpdateScheduleClick(val schedule: TodoSchedule) : HomeIntent
    data class OnDeleteScheduleClick(val schedule: TodoSchedule) : HomeIntent
    data class OnDelayScheduleClick(val schedule: TodoSchedule) : HomeIntent
}
