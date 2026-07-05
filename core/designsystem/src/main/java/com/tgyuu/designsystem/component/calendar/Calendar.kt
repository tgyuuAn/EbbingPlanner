package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.layout.Arrangement
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
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    val weekInitialPage = CALENDAR_PAGE_COUNT / 2
    val weekPagerState = rememberPagerState(
        initialPage = weekInitialPage,
        pageCount = { CALENDAR_PAGE_COUNT },
    )
    val weekOffset = weekPagerState.currentPage - weekInitialPage

    // 월간 페이저 스크롤 → 현재 표시 날짜 업데이트
    LaunchedEffect(monthPagerState.currentPage) {
        if (!showWeekOnly) {
            calendarState.currentDisplayDate =
                calendarState.originSelectedDate.plusMonths(monthOffset.toLong())
        }
    }

    // 주간 페이저 스크롤 → 현재 표시 날짜 업데이트
    LaunchedEffect(weekPagerState.currentPage) {
        if (showWeekOnly) {
            val originWeekStart = getWeekStart(calendarState.originSelectedDate, startFromMonday)
            calendarState.currentDisplayDate = originWeekStart.plusWeeks(weekOffset.toLong())
        }
    }

    // 선택 날짜 변경 시 주간 페이저 동기화 (하단 리스트 스와이프 포함)
    LaunchedEffect(calendarState.selectedDate) {
        if (showWeekOnly) {
            val targetWeekOffset = weeksBetween(
                from = calendarState.originSelectedDate,
                to = calendarState.selectedDate,
                startFromMonday = startFromMonday,
            )
            if (targetWeekOffset != weekOffset) {
                weekPagerState.animateScrollToPage(weekInitialPage + targetWeekOffset)
            }
        }
    }

    // 뷰 모드 전환 시 페이저 위치 동기화
    LaunchedEffect(showWeekOnly) {
        if (showWeekOnly) {
            val targetWeekOffset = weeksBetween(
                from = calendarState.originSelectedDate,
                to = calendarState.selectedDate,
                startFromMonday = startFromMonday,
            )
            weekPagerState.scrollToPage(weekInitialPage + targetWeekOffset)
        } else {
            val targetMonthOffset = yearMonthDiff(
                from = calendarState.originSelectedDate,
                to = calendarState.currentDisplayDate,
            )
            monthPagerState.scrollToPage(monthInitialPage + targetMonthOffset)
        }
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

@Composable
fun rememberCalendarState(originSelectedDate: LocalDate = LocalDate.now()): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(originSelectedDate)
    }
}
