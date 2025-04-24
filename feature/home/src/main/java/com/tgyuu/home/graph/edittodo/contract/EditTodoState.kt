package com.tgyuu.home.graph.edittodo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.home.graph.InputState
import java.time.DayOfWeek
import java.time.LocalDate

data class EditTodoState(
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val titleInputState: InputState = InputState.DEFAULT,
    val priority: String? = null,
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
    val repeatCycle: RepeatCycle = RepeatCycle.SAME_DAY,
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val isSaveEnabled = title.isNotEmpty()
    val isInputFieldIncomplete: Boolean = titleInputState == InputState.WARNING

    val schedules: List<LocalDate>
        get() = repeatCycle.intervals.fold(mutableListOf()) { acc, interval ->
            val base = acc.lastOrNull() ?: selectedDate

            val next = base
                .plusDays(interval.toLong())
                .nextValidDate()

            acc.apply { add(next) }
        }

    private fun LocalDate.nextValidDate(): LocalDate {
        var d = this
        while (d.dayOfWeek in restDays) {
            d = d.plusDays(1)
        }
        return d
    }
}
