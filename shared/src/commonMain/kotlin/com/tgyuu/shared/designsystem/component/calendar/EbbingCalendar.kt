package com.tgyuu.shared.designsystem.component.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.tgyuu.shared.designsystem.component.icon.EbbingSyncIcon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.now
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.calendar_go_today
import ebbingplanner.shared.generated.resources.calendar_year_month
import ebbingplanner.shared.generated.resources.sync_sync
import org.jetbrains.compose.resources.stringResource

private const val CALENDAR_PAGE_COUNT = 12_001

@Composable
fun EbbingCalendar(
    calendarState: CalendarState,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    modifier: Modifier = Modifier,
    startFromMonday: Boolean = false,
    showWeekOnly: Boolean = false,
    onSelectDate: (LocalDate) -> Unit = {},
    onGotoTodayClick: () -> Unit = {},
    showSyncButton: Boolean = true,
    onSyncClick: () -> Unit = {},
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

    // 월간 페이저 스크롤 -> 현재 표시 날짜 업데이트
    LaunchedEffect(monthPagerState.currentPage) {
        if (!showWeekOnly) {
            calendarState.currentDisplayDate = calendarState.originSelectedDate.plus(
                value = monthOffset.toLong(),
                unit = DateTimeUnit.MONTH,
            )
        }
    }

    // 주간 페이저 스크롤 -> 현재 표시 날짜 업데이트
    LaunchedEffect(weekPagerState.currentPage) {
        if (showWeekOnly) {
            val originWeekStart = getWeekStart(calendarState.originSelectedDate, startFromMonday)
            calendarState.currentDisplayDate =
                originWeekStart.plus(weekOffset * 7, DateTimeUnit.DAY)
        }
    }

    // 선택 날짜 변경 시 주간 페이저 동기화
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
        )

        CalendarHeader(startFromMonday = startFromMonday)

        if (showWeekOnly) {
            HorizontalPager(
                state = weekPagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { pageIndex ->
                val pageOffset = pageIndex - weekInitialPage
                val weekStart = getWeekStart(calendarState.originSelectedDate, startFromMonday)
                    .plus(pageOffset * 7, DateTimeUnit.DAY)
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
                            to = selectedDate,
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

// ── Controller ──

@Composable
private fun CalendarController(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onGotoTodayClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSyncButton: Boolean = true,
    onSyncClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        val today = LocalDate.now()
        val isOnToday = currentDate.year == today.year &&
            currentDate.monthNumber == today.monthNumber &&
            (selectedDate == null || selectedDate == today)

        IconButton(onClick = onGotoTodayClick) {
            if (!isOnToday) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(Res.string.calendar_go_today),
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        Text(
            text = stringResource(Res.string.calendar_year_month, currentDate.year, currentDate.monthNumber),
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
        )

        if (showSyncButton) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onSyncClick),
            ) {
                Icon(
                    imageVector = EbbingSyncIcon,
                    contentDescription = stringResource(Res.string.sync_sync),
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier.size(28.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

// ── Header ──

@Composable
private fun CalendarHeader(
    startFromMonday: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val days = if (startFromMonday) EbbingDayOfWeekMonday else EbbingDayOfWeekSunday
    Row(modifier = modifier.fillMaxWidth()) {
        days.forEach { weekday ->
            Text(
                text = weekday.toKorean(),
                textAlign = TextAlign.Center,
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Month Body ──

@Composable
private fun CalendarBody(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    onDateSelect: (LocalDate) -> Unit,
    startFromMonday: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(
            items = getCalendarDates(currentDate, startFromMonday),
            key = { it.date.toString() },
        ) {
            CalendarDayItem(
                calendarDate = it,
                selectedDate = selectedDate,
                events = schedulesByDateMap[it.date] ?: emptyList(),
                onDateSelect = onDateSelect,
            )
        }
    }
}

// ── Week Body ──

@Composable
private fun WeekCalendarBody(
    weekReferenceDate: LocalDate,
    selectedDate: LocalDate,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    onDateSelect: (LocalDate) -> Unit,
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        getWeekDates(weekReferenceDate, startFromMonday).forEach { calendarDate ->
            CalendarDayItem(
                calendarDate = calendarDate,
                selectedDate = selectedDate,
                events = schedulesByDateMap[calendarDate.date] ?: emptyList(),
                onDateSelect = onDateSelect,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Day Item ──

@Composable
private fun CalendarDayItem(
    calendarDate: CalendarDate,
    selectedDate: LocalDate?,
    events: List<TodoScheduleUiModel>,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayItemColor by animateColorAsState(
        targetValue = if (calendarDate.date == selectedDate) EbbingTheme.colors.black
        else Color.Transparent,
        label = "dayItemColor",
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = dayItemColor,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onDateSelect(calendarDate.date) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 4.dp),
        ) {
            var isOverflow by remember { mutableStateOf(false) }
            val textColor = when {
                calendarDate.date == selectedDate -> EbbingTheme.colors.white
                !calendarDate.isCurrentMonth -> EbbingTheme.colors.dark3
                else -> EbbingTheme.colors.black
            }

            if (isOverflow) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Today",
                    tint = textColor,
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Text(
                    text = if (calendarDate.date == LocalDate.now()) "Today" else "",
                    style = EbbingTheme.typography.captionR12,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor,
                    onTextLayout = { result -> isOverflow = result.hasVisualOverflow },
                )
            }

            Text(
                text = calendarDate.dayOfMonth.toString(),
                style = EbbingTheme.typography.bodyMM,
                textAlign = TextAlign.Center,
                color = textColor,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 2.dp,
                    alignment = Alignment.CenterHorizontally,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            ) {
                events.map { it.color }
                    .distinct()
                    .take(4)
                    .forEach {
                        Spacer(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(it))
                        )
                    }
            }
        }
    }
}
