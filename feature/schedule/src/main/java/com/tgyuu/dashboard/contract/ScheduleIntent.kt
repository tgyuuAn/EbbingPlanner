package com.tgyuu.dashboard.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.TodoTagUiModel

sealed interface ScheduleIntent : UiIntent {
    data class OnTagClick(val tag: TodoTagUiModel) : ScheduleIntent
    data class OnInfoClick(val todoInfo: TodoInfoUiModel) : ScheduleIntent
    data class OnScheduleClick(val schedule: TodoScheduleUiModel) : ScheduleIntent
}
