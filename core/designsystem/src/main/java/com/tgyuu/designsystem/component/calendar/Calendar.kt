package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.metrics.performance.PerformanceMetricsState
import com.tgyuu.domain.model.TodoSchedule
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun rememberMetricsStateHolder(): PerformanceMetricsState.Holder {
    val view = LocalView.current
    return remember(view) { PerformanceMetricsState.getHolderForHierarchy(view) }
}

@Composable
fun EbbingCalendar(
    calendarState: CalendarState,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
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

    val metricsHolder = rememberMetricsStateHolder()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .collect { scrolling ->
                metricsHolder.state?.run {
                    if (scrolling) putState("Pager", "Scrolling")
                    else removeState("Pager")
                }
            }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        val scope = rememberCoroutineScope()

        CalendarController(
            currentDate = calendarState.currentDisplayDate,
            onGotoTodayClick = {
                scope.launch {
                    pagerState.animateScrollToPage(initialPage)
                    calendarState.onDateSelect(LocalDate.now())
                    onDateSelect(LocalDate.now())
                }
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
                schedulesByDateMap = schedulesByDateMap,
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
