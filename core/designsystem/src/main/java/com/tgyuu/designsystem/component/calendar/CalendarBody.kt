package com.tgyuu.designsystem.component.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

@Composable
internal fun CalendarBody(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.semantics { contentDescription = "달력 바디" },
    ) {
        items(items = getCalendarDates(currentDate)) {
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
    events: List<TodoSchedule>,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dayItemColor by animateColorAsState(
        if (calendarDate.date == selectedDate) EbbingTheme.colors.black else Color.Transparent
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
            val textColor by animateColorAsState(
                when {
                    !calendarDate.isCurrentMonth -> EbbingTheme.colors.dark3
                    calendarDate.date == selectedDate -> EbbingTheme.colors.white
                    else -> EbbingTheme.colors.black
                }
            )

            Text(
                text = if (calendarDate.date == LocalDate.now()) "Today" else "",
                style = EbbingTheme.typography.captionM,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )

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
