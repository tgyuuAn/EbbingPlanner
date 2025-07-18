package com.tgyuu.home.graph.edittodo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

data class EditTodoState(
    val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> = emptyMap(),
    val originSchedule: TodoSchedule? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val priority: String? = null,
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
    val repeatCycleList: List<RepeatCycle> = DefaultRepeatCycles,
    val repeatCycle: RepeatCycle = DefaultRepeatCycles.first(),
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
    val schedules: List<LocalDate>
        get() = generateValidSchedules(
            baseDate = selectedDate,
            intervals = repeatCycle.intervals,
            restDays = restDays
        )
}
