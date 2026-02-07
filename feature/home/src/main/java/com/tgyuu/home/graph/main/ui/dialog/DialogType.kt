package com.tgyuu.home.graph.main.ui.dialog

import com.tgyuu.designsystem.model.TodoScheduleUiModel

sealed class DialogType(open val schedule: TodoScheduleUiModel) {
    data class ConfirmDeleteSingle(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
    data class ConfirmDeleteRemaining(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
    data class ConfirmDelay(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
    data class ConfirmDeleteMemo(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
}
