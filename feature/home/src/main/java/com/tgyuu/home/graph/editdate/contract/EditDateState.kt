package com.tgyuu.home.graph.editdate.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.common.generateValidSchedules
import com.tgyuu.common.now
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class EditDateState(
    val title: String = "",
    val originTagColor: Int = 0XFFBBE1FA.toInt(),
    val selectedDate: LocalDate = LocalDate.now(),
    val repeatCycleList: List<RepeatCycle> = DefaultRepeatCycles,
    val repeatCycle: RepeatCycle = DefaultRepeatCycles.first(),
    val restDays: Set<DayOfWeek> = emptySet(),
) : UiState {
    val schedules: List<LocalDate>
        get() = generateValidSchedules(
            baseDate = selectedDate,
            intervals = repeatCycle.intervals,
            restDays = restDays
        )
}
