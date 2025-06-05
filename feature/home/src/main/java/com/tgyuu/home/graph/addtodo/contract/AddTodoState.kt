package com.tgyuu.home.graph.addtodo.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate

data class AddTodoState(
    val selectedDate: LocalDate = LocalDate.now(),
    val title: String = "",
    val titleInputState: InputState = InputState.DEFAULT,
    val priority: String? = null,
    val tag: TodoTag = DefaultTodoTag,
    val tagList: List<TodoTag> = emptyList(),
    val repeatCycleList: List<RepeatCycle> = DefaultRepeatCycles,
    val repeatCycle: RepeatCycle = DefaultRepeatCycles.first(),
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
