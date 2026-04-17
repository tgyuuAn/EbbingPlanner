package com.tgyuu.shared.designsystem.component.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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

@Composable
fun EbbingCalendar(
    calendarState: CalendarState,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    modifier: Modifier = Modifier,
    startFromMonday: Boolean = false,
    onSelectDate: (LocalDate) -> Unit = {},
    onSyncClick: (() -> Unit)? = null,
) {
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE },
    )
    val currentOffset = pagerState.currentPage - initialPage

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
            onGotoTodayClick = {
                scope.launch {
                    pagerState.animateScrollToPage(initialPage)
                    calendarState.onDateSelect(LocalDate.now())
                    onSelectDate(LocalDate.now())
                }
            },
            onSyncClick = onSyncClick,
        )

        CalendarHeader(startFromMonday = startFromMonday)

        HorizontalPager(
            state = pagerState,
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

                    if (selectedOffset != currentOffset) {
                        scope.launch {
                            pagerState.animateScrollToPage(initialPage + selectedOffset)
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

@Composable
private fun CalendarController(
    currentDate: LocalDate,
    onGotoTodayClick: () -> Unit,
    onSyncClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        IconButton(onClick = onGotoTodayClick) {
            if (currentDate.year != LocalDate.now().year || currentDate.monthNumber != LocalDate.now().monthNumber) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "오늘로 이동",
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        Text(
            text = "${currentDate.year}년 ${currentDate.monthNumber}월",
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
        )

        if (onSyncClick != null) {
            IconButton(onClick = onSyncClick) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "동기화",
                    tint = EbbingTheme.colors.black,
                    modifier = Modifier.size(16.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

@Composable
private fun CalendarHeader(
    startFromMonday: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val days = if (startFromMonday) EbbingDayOfWeekMonday else EbbingDayOfWeekSunday
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        days.forEach { weekday ->
            val weekDayText = weekday.toKorean()

            Text(
                text = weekDayText,
                textAlign = TextAlign.Center,
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

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
                !calendarDate.isCurrentMonth -> EbbingTheme.colors.dark3
                calendarDate.date == selectedDate -> EbbingTheme.colors.white
                else -> EbbingTheme.colors.black
            }

            if (isOverflow) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Today",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = if (calendarDate.date == LocalDate.now()) "Today" else "",
                    style = EbbingTheme.typography.captionM,
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
