package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent
    data class OnCheckedChange(val schedule: TodoSchedule) : HomeIntent
    data class OnSortTypeClick(val content: BottomSheetContent) : HomeIntent
    data class OnUpdateSortType(val sortType: SortType) : HomeIntent
    data class OnEditScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnDeleteScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnDeleteSingleClick(val schedule: TodoSchedule) : HomeIntent
    data class OnDeleteRemainingClick(val schedule: TodoSchedule) : HomeIntent
    data class OnUpdateScheduleClick(val schedule: TodoSchedule) : HomeIntent
    data class OnDelayScheduleClick(val schedule: TodoSchedule) : HomeIntent
    data class OnMemoClick(val schedule: TodoSchedule) : HomeIntent
    data class OnDeleteMemoClick(val schedule: TodoSchedule) : HomeIntent
    data object OnSyncClick : HomeIntent
}
