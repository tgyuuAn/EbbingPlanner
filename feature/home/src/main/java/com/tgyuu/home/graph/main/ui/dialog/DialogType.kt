package com.tgyuu.home.graph.main.ui.dialog

import com.tgyuu.domain.model.TodoSchedule

sealed class DialogType(open val schedule: TodoSchedule) {
    data class Delete(override val schedule: TodoSchedule) : DialogType(schedule)
    data class DeleteAll(override val schedule: TodoSchedule) : DialogType(schedule)
    data class Delay(override val schedule: TodoSchedule) : DialogType(schedule)
    data class DeleteMemo(override val schedule: TodoSchedule) : DialogType(schedule)
}
