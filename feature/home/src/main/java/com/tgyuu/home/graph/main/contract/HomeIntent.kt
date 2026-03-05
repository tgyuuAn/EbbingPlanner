package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.SortType
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnCurrentDateChanged(val currentDate: LocalDate) : HomeIntent
    data class OnAddTodoClick(val selectedDate: LocalDate) : HomeIntent
    data class OnCheckChanged(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnSortTypeClick(val content: BottomSheetContent) : HomeIntent
    data class OnUpdateSortType(val sortType: SortType) : HomeIntent
    data class OnEditScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnDeleteScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnDeleteSingleClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnDeleteRemainingClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnUpdateScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnUpdateInfoClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnUpdateDateClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnDelayScheduleClick(val content: BottomSheetContent) : HomeIntent
    data class OnDelaySingleClick(
        val schedule: TodoScheduleUiModel,
        val includeRestDays: Boolean = false,
    ) : HomeIntent
    data class OnDelayAllClick(
        val schedule: TodoScheduleUiModel,
        val includeRestDays: Boolean = false,
    ) : HomeIntent
    data class OnMemoClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data class OnDeleteMemoClick(val schedule: TodoScheduleUiModel) : HomeIntent
    data object OnSyncClick : HomeIntent
    data object OnWidgetNudgeDismiss : HomeIntent
}
