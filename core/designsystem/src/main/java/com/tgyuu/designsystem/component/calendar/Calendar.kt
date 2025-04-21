package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun EbbingCalendar(
    calendarState: CalendarState,
    modifier: Modifier = Modifier,
    onDateSelect: (LocalDate) -> Unit = {},
) {
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE },
    )
    val currentOffset = pagerState.currentPage - initialPage

    LaunchedEffect(pagerState.currentPage) {
        val newDate = calendarState.originSelectedDate.plusMonths(currentOffset.toLong())
        calendarState.currentDisplayDate = newDate
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        val scope = rememberCoroutineScope()

        CalendarController(
            currentDate = calendarState.currentDisplayDate,
            onGotoTodayClick = {
                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            },
        )
        CalendarHeader()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { _ ->
            CalendarBody(
                currentDate = calendarState.currentDisplayDate,
                selectedDate = calendarState.selectedDate,
                onDateSelect = { selectedDate ->
                    val selectedOffset = yearMonthDiff(
                        from = calendarState.originSelectedDate,
                        to = selectedDate
                    )

                    if (selectedOffset != currentOffset) {
                        scope.launch {
                            pagerState.animateScrollToPage(initialPage + selectedOffset)
                            calendarState.onDateSelect(selectedDate)
                            onDateSelect(selectedDate)
                        }
                    } else {
                        calendarState.onDateSelect(selectedDate)
                        onDateSelect(selectedDate)
                    }
                },
            )
        }
    }
}

@Composable
fun rememberCalendarState(originSelectedDate: LocalDate = LocalDate.now()): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(originSelectedDate)
    }
}
