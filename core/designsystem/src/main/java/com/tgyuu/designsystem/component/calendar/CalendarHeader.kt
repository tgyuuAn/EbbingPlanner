package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun CalendarHeader(
    startFromMonday: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "달력 헤더" },
    ) {
        getEbbingDayOfWeek(startFromMonday).forEachIndexed { idx, weekday ->
            val weekDayText = weekday.toKorean()

            Text(
                text = weekDayText,
                textAlign = TextAlign.Center,
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "${weekDayText}_${idx}" },
            )
        }
    }
}
