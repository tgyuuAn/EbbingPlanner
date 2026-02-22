package com.tgyuu.home.graph.main.ui.dialog

import com.tgyuu.designsystem.model.TodoScheduleUiModel
import java.time.DayOfWeek
import java.time.LocalDate

sealed class DialogType(open val schedule: TodoScheduleUiModel) {
    data class ConfirmDeleteSingle(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
    data class ConfirmDeleteRemaining(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
    data class ConfirmDelay(
        override val schedule: TodoScheduleUiModel,
        val restDays: Set<DayOfWeek> = emptySet(),
        val expectedDateExcludingRestDays: LocalDate? = null,
        val expectedDateIncludingRestDays: LocalDate? = null,
    ) : DialogType(schedule)
    data class ConfirmDelayAll(
        override val schedule: TodoScheduleUiModel,
        val restDays: Set<DayOfWeek> = emptySet(),
    ) : DialogType(schedule)
    data class ConfirmDeleteMemo(override val schedule: TodoScheduleUiModel) : DialogType(schedule)
}
