package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import java.time.LocalDate

@Composable
internal fun CalendarBody(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    onDateSelect: (LocalDate) -> Unit,
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    val bodyDescription = stringResource(R.string.ds_cd_calendar_body)

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

@Composable
internal fun WeekCalendarBody(
    weekReferenceDate: LocalDate,
    selectedDate: LocalDate,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    onDateSelect: (LocalDate) -> Unit,
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    val weekBodyDescription = stringResource(R.string.ds_cd_calendar_week_body)

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

@Composable
internal fun CalendarDayItem(
    calendarDate: CalendarDate,
    selectedDate: LocalDate?,
    events: List<TodoScheduleUiModel>,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = calendarDate.date == selectedDate
    val isToday = calendarDate.date == LocalDate.now()

    // 선택 = 검은 원(Fill/Focused), 오늘(미선택) = 회색 원(Fill/Disabled), 그 외 = 없음
    val circleColor = ebbingAnimateColorAsState(
        targetValue = when {
            isSelected -> EbbingTheme.colors.fillFocused
            isToday -> EbbingTheme.colors.fillDisabled
            else -> Color.Transparent
        }
    )
    val numberColor = when {
        isSelected || isToday -> EbbingTheme.colors.textOnPrimary
        !calendarDate.isCurrentMonth -> EbbingTheme.colors.textDisabled
        else -> EbbingTheme.colors.textOnBackground
    }
    val numberStyle =
        if (isSelected || isToday) EbbingTheme.typography.heading14B
        else EbbingTheme.typography.body14M

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
