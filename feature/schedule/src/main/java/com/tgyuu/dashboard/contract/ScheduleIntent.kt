package com.tgyuu.dashboard.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.designsystem.model.TodoScheduleUiModel

sealed interface ScheduleIntent : UiIntent {
    data class OnToggleTagExpand(val tagId: Int) : ScheduleIntent
    data class OnToggleInfoExpand(val infoId: Int) : ScheduleIntent
    data class OnScheduleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
    data object OnNavigateToAddTodo : ScheduleIntent
    data class OnShowBottomSheet(val content: BottomSheetContent) : ScheduleIntent
    data class OnReplaceBottomSheet(val content: BottomSheetContent) : ScheduleIntent
    data class OnSaveTag(val tagId: Int, val name: String, val color: Int) : ScheduleIntent
    data class OnDeleteTag(val tagId: Int) : ScheduleIntent
    data class OnRequestDeleteTag(val tagId: Int, val tagName: String) : ScheduleIntent
    data object OnClearPendingDeleteTag : ScheduleIntent

    // 수정하기
    data class OnUpdateInfoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
    data class OnUpdateDateClick(val schedule: TodoScheduleUiModel) : ScheduleIntent

    // 삭제하기
    data class OnDeleteSingleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
    data class OnDeleteRemainingClick(val schedule: TodoScheduleUiModel) : ScheduleIntent

    // 미루기
    data class OnDelaySingleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
    data class OnDelayAllClick(val schedule: TodoScheduleUiModel) : ScheduleIntent

    // 메모
    data class OnMemoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
    data class OnDeleteMemoClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
}
