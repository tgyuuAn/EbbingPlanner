package com.tgyuu.home.graph.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toRelativeDayDescription
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.LocalDate

@Composable
internal fun ScheduleContent(
    schedules: List<LocalDate>,
    modifier: Modifier = Modifier,
) {
    EbbingVisibleAnimation(schedules.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "${schedules.size} 개의 학습 일정",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 32.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.light3)
            ) {
                schedules.forEachIndexed { idx, item ->
                    ScheduleCard(
                        idx = idx + 1,
                        schedule = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp,
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    idx: Int,
    schedule: LocalDate,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = idx.toString(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = schedule.toRelativeDayDescription(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )
    }
}

@Composable
internal fun ScheduleCheckContent(
    schedules: List<LocalDate>,
    isDoneSchedules: List<Boolean>,
    colorValue: Int,
    onCheckSchedule: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    EbbingVisibleAnimation(schedules.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "${schedules.size} 개의 학습 일정",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 32.dp)

            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.light3)
            ) {
                schedules.forEachIndexed { idx, item ->
                    ScheduleCheckCard(
                        idx = idx + 1,
                        isChecked = isDoneSchedules[idx],
                        colorValue = colorValue,
                        schedule = item,
                        onCheckSchedule = { onCheckSchedule(idx) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 16.dp,
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleCheckCard(
    idx: Int,
    colorValue: Int,
    isChecked: Boolean,
    schedule: LocalDate,
    modifier: Modifier = Modifier,
    onCheckSchedule: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = idx.toString(),
                style = EbbingTheme.typography.bodyMSB,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
                style = EbbingTheme.typography.bodyMSB,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = schedule.toRelativeDayDescription(),
                style = EbbingTheme.typography.bodyMSB,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.black,
            )
        }

        EbbingCheck(
            checked = isChecked,
            colorValue = colorValue,
            onCheckedChange = { onCheckSchedule(idx) },
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}
