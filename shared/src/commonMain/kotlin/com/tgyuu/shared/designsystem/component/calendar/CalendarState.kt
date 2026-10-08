package com.tgyuu.shared.designsystem.component.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.tgyuu.shared.common.now
import kotlinx.datetime.LocalDate

class CalendarState(val originSelectedDate: LocalDate = LocalDate.now()) {
    var currentDisplayDate by mutableStateOf(originSelectedDate)
    var selectedDate by mutableStateOf<LocalDate>(originSelectedDate)

    fun onDateSelect(date: LocalDate) {
        selectedDate = date
        currentDisplayDate = date
    }

    companion object {
        val Saver: Saver<CalendarState, *> = listSaver(
            save = { listOf(it.originSelectedDate.toString()) },
            restore = {
                CalendarState(originSelectedDate = LocalDate.parse(it[0]))
            }
        )
    }
}

@Composable
fun rememberCalendarState(originSelectedDate: LocalDate = LocalDate.now()): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(originSelectedDate)
    }
}
