package com.tgyuu.home.graph.add.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

data class AddTodoState(
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
    val repeatCycle: RepeatCycle = RepeatCycle.SAME_DAY,
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()

    val schedules: List<LocalDate>
        get() = repeatCycle.intervals.map { interval ->
            selectedDate
                .plusDays(interval.toLong())
                .nextValidDate()
        }

    private fun LocalDate.nextValidDate(): LocalDate {
        var d = this
        while (d.dayOfWeek in restDays) {
            d = d.plusDays(1)
        }
        return d
    }
}
