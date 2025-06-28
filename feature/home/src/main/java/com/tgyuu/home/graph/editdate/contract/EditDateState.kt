package com.tgyuu.home.graph.editdate.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import java.time.DayOfWeek
import java.time.LocalDate

data class EditDateState(
    val title: String = "",
    val originTagColor: Int = 0XFFBBE1FA.toInt(),
    val selectedDate: LocalDate = LocalDate.now(),
    val repeatCycleList: List<RepeatCycle> = DefaultRepeatCycles,
    val repeatCycle: RepeatCycle = DefaultRepeatCycles.first(),
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
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
