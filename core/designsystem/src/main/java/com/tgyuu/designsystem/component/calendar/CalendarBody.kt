package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
internal fun CalendarBody(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
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
                onDateSelect = onDateSelect,
            )
        }
    }
}

@Composable
private fun CalendarDayItem(
    calendarDate: CalendarDate,
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = when {
        !calendarDate.isCurrentMonth -> Color.LightGray
        calendarDate.date == selectedDate -> Color.White
        else -> Color.DarkGray
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (calendarDate.date == selectedDate) Color.DarkGray else Color.Transparent,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .clickable { onDateSelect(calendarDate.date) },
    ) {
        Text(
            text = calendarDate.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            color = textColor,
        )
    }
}
