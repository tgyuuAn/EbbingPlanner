package com.tgyuu.home.graph.main.ui.dialog

import com.tgyuu.domain.model.TodoSchedule

sealed class DialogType(open val schedule: TodoSchedule) {
    data class ConfirmDeleteSingle(override val schedule: TodoSchedule) : DialogType(schedule)
    data class ConfirmDeleteRemaining(override val schedule: TodoSchedule) : DialogType(schedule)
    data class ConfirmDelay(override val schedule: TodoSchedule) : DialogType(schedule)
    data class ConfirmDeleteMemo(override val schedule: TodoSchedule) : DialogType(schedule)
}
