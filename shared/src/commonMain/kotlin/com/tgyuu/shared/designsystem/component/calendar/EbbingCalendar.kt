package com.tgyuu.shared.designsystem.component.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.now
import com.tgyuu.shared.designsystem.component.EbbingTextToggle
import com.tgyuu.shared.designsystem.component.icon.EbbingSyncIcon
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.calendar_cd_body
import ebbingplanner.shared.generated.resources.calendar_cd_controller
import ebbingplanner.shared.generated.resources.calendar_cd_header
import ebbingplanner.shared.generated.resources.calendar_cd_week_body
import ebbingplanner.shared.generated.resources.calendar_go_today
import ebbingplanner.shared.generated.resources.calendar_today
import ebbingplanner.shared.generated.resources.calendar_view_month
import ebbingplanner.shared.generated.resources.calendar_view_week
import ebbingplanner.shared.generated.resources.calendar_year_month
import ebbingplanner.shared.generated.resources.ic_return
import ebbingplanner.shared.generated.resources.sync_sync
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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

        Box(modifier = Modifier.fillMaxWidth().animateContentSize()) {
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
    showViewToggle: Boolean = false,
    isWeekView: Boolean = false,
    onViewToggle: (Boolean) -> Unit = {},
) {
    val controllerDescription = stringResource(Res.string.calendar_cd_controller)
    val today = LocalDate.now()
    val isOnToday = currentDate.year == today.year &&
        currentDate.monthNumber == today.monthNumber &&
        (selectedDate == null || selectedDate == today)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .semantics { contentDescription = controllerDescription },
    ) {
        Text(
            text = stringResource(Res.string.calendar_year_month, currentDate.year, currentDate.monthNumber),
            style = EbbingTheme.typography.headingMSB,
            color = EbbingTheme.colors.black,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!isOnToday) {
                TodayButton(onClick = onGotoTodayClick)
            } else if (showSyncButton) {
                SyncButton(onClick = onSyncClick)
            }

            if (showViewToggle) {
                EbbingTextToggle(
                    firstLabel = stringResource(Res.string.calendar_view_month),
                    secondLabel = stringResource(Res.string.calendar_view_week),
                    selectedFirst = !isWeekView,
                    onSelectedChange = { toMonth -> onViewToggle(!toMonth) },
                )
            }
        }
    }
}

@Composable
private fun TodayButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(100.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(shape)
            .background(EbbingTheme.colors.background)
            .border(width = 1.dp, color = EbbingTheme.colors.light2, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_return),
            contentDescription = stringResource(Res.string.calendar_go_today),
            tint = EbbingTheme.colors.dark2,
            modifier = Modifier
                .size(20.dp)
                .padding(3.dp),
        )
        Text(
            text = stringResource(Res.string.calendar_today),
            style = EbbingTheme.typography.captionR12,
            color = EbbingTheme.colors.dark1,
        )
    }
}

@Composable
private fun SyncButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = EbbingSyncIcon,
            contentDescription = stringResource(Res.string.sync_sync),
            tint = EbbingTheme.colors.black,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ── Header ──

@Composable
private fun CalendarHeader(
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    val headerDescription = stringResource(Res.string.calendar_cd_header)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = headerDescription },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val days = if (startFromMonday) EbbingDayOfWeekMonday else EbbingDayOfWeekSunday
        days.forEachIndexed { idx, weekday ->
            val weekDayText = weekday.toLocalizedShort()

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(34.dp)
                    .semantics { contentDescription = "${weekDayText}_${idx}" },
            ) {
                Text(
                    text = weekDayText,
                    textAlign = TextAlign.Center,
                    style = EbbingTheme.typography.captionR12,
                    color = EbbingTheme.colors.dark3,
                )
            }
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
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    val bodyDescription = stringResource(Res.string.calendar_cd_body)

    // 고정 34dp 셀 + SpaceBetween 으로 첫 셀을 좌측(월 제목)과 정렬한다.
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = bodyDescription },
    ) {
        getCalendarDates(currentDate, startFromMonday).chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                week.forEach { calendarDate ->
                    CalendarDayItem(
                        calendarDate = calendarDate,
                        selectedDate = selectedDate,
                        events = schedulesByDateMap[calendarDate.date] ?: emptyList(),
                        onDateSelect = onDateSelect,
                    )
                }
            }
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
    val weekBodyDescription = stringResource(Res.string.calendar_cd_week_body)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .semantics { contentDescription = weekBodyDescription },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        getWeekDates(weekReferenceDate, startFromMonday).forEach { calendarDate ->
            CalendarDayItem(
                calendarDate = calendarDate,
                selectedDate = selectedDate,
                events = schedulesByDateMap[calendarDate.date] ?: emptyList(),
                onDateSelect = onDateSelect,
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
    val isSelected = calendarDate.date == selectedDate
    val isToday = calendarDate.date == LocalDate.now()

    // 선택 = 검은 원, 오늘(미선택) = 회색 원, 그 외 = 없음
    val circleColor by animateColorAsState(
        targetValue = when {
            isSelected -> EbbingTheme.colors.black
            isToday -> EbbingTheme.colors.light1
            else -> Color.Transparent
        },
        label = "dayItemColor",
    )
    val numberColor = when {
        isSelected || isToday -> EbbingTheme.colors.white
        !calendarDate.isCurrentMonth -> EbbingTheme.colors.dark3
        else -> EbbingTheme.colors.black
    }
    val numberStyle =
        if (isSelected || isToday) EbbingTheme.typography.headingSB
        else EbbingTheme.typography.bodySM

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .width(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onDateSelect(calendarDate.date) }
            .padding(vertical = 4.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(circleColor),
        ) {
            Text(
                text = calendarDate.dayOfMonth.toString(),
                style = numberStyle,
                textAlign = TextAlign.Center,
                color = numberColor,
            )
        }

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
