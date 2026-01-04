package com.tgyuu.designsystem.component.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.common.now
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private const val CALENDAR_PAGE_COUNT = 12_001 // ±500년(월), ±115년(주)

@Composable
fun EbbingCalendar(
    calendarState: CalendarState,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    modifier: Modifier = Modifier,
    showSyncButton: Boolean = true,
    startFromMonday: Boolean = false,
    showWeekOnly: Boolean = false,
    showViewToggle: Boolean = false,
    onSelectDate: (LocalDate) -> Unit = {},
    onGotoTodayClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    onViewToggle: (Boolean) -> Unit = {},
) {
    val monthInitialPage = CALENDAR_PAGE_COUNT / 2
    val monthPagerState = rememberPagerState(
        initialPage = monthInitialPage,
        pageCount = { CALENDAR_PAGE_COUNT },
    )
    val monthOffset = monthPagerState.currentPage - monthInitialPage

    LaunchedEffect(pagerState.currentPage) {
        val newDate = calendarState.originSelectedDate.plus(
            value = currentOffset.toLong(),
            unit = DateTimeUnit.MONTH,
        )
        calendarState.currentDisplayDate = newDate
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        val scope = rememberCoroutineScope()

        CalendarController(
            currentDate = calendarState.currentDisplayDate,
            selectedDate = calendarState.selectedDate,
            onGotoTodayClick = {
                onGotoTodayClick()
                scope.launch {
                    if (showWeekOnly) {
                        val todayWeekOffset = weeksBetween(
                            from = calendarState.originSelectedDate,
                            to = LocalDate.now(),
                            startFromMonday = startFromMonday,
                        )
                        weekPagerState.animateScrollToPage(weekInitialPage + todayWeekOffset)
                    } else {
                        monthPagerState.animateScrollToPage(monthInitialPage)
                    }
                    calendarState.onDateSelect(LocalDate.now())
                    onSelectDate(LocalDate.now())
                }
            },
            showSyncButton = showSyncButton,
            onSyncClick = onSyncClick,
            showViewToggle = showViewToggle,
            isWeekView = showWeekOnly,
            onViewToggle = onViewToggle,
        )

        CalendarHeader(startFromMonday = startFromMonday)

        Box(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            if (showWeekOnly) {
            HorizontalPager(
                state = weekPagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { pageIndex ->
                val pageOffset = pageIndex - weekInitialPage
                val weekStart = getWeekStart(calendarState.originSelectedDate, startFromMonday)
                    .plusWeeks(pageOffset.toLong())
                WeekCalendarBody(
                    weekReferenceDate = weekStart,
                    selectedDate = calendarState.selectedDate,
                    schedulesByDateMap = schedulesByDateMap,
                    startFromMonday = startFromMonday,
                    onDateSelect = { selectedDate ->
                        calendarState.onDateSelect(selectedDate)
                        onSelectDate(selectedDate)
                    },
                )
            }
        } else {
            HorizontalPager(
                state = monthPagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { _ ->
                CalendarBody(
                    currentDate = calendarState.currentDisplayDate,
                    selectedDate = calendarState.selectedDate,
                    schedulesByDateMap = schedulesByDateMap,
                    startFromMonday = startFromMonday,
                    onDateSelect = { selectedDate ->
                        val selectedOffset = yearMonthDiff(
                            from = calendarState.originSelectedDate,
                            to = selectedDate
                        )
                        if (selectedOffset != monthOffset) {
                            scope.launch {
                                monthPagerState.animateScrollToPage(monthInitialPage + selectedOffset)
                                calendarState.onDateSelect(selectedDate)
                                onSelectDate(selectedDate)
                            }
                        } else {
                            calendarState.onDateSelect(selectedDate)
                            onSelectDate(selectedDate)
                        }
                    },
                )
            }
        }
        }
    }
}

@Composable
fun rememberCalendarState(originSelectedDate: LocalDate = LocalDate.now()): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(originSelectedDate)
    }
}
