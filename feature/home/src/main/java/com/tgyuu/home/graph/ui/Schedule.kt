package com.tgyuu.home.graph.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import kotlinx.datetime.LocalDate

@Composable
internal fun ScheduleContent(
    schedules: List<LocalDate>,
    modifier: Modifier = Modifier,
) {
    EbbingVisibleAnimation(schedules.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "${schedules.size} 개의 학습 일정",
                style = EbbingTheme.typography.heading20B,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier.padding(top = 32.dp),
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.fillNormal)
            ) {
                itemsIndexed(schedules) { idx, item ->
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
            style = EbbingTheme.typography.body16M,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.textOnBackground,
        )

        Text(
            text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
            style = EbbingTheme.typography.body16M,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.textOnBackground,
        )

        Text(
            text = schedule.toRelativeDayDescription(),
            style = EbbingTheme.typography.body16M,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.textOnBackground,
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
                style = EbbingTheme.typography.heading20B,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier.padding(top = 32.dp)

            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.fillNormal)
            ) {
                itemsIndexed(schedules) { idx, item ->
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
                style = EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textOnBackground,
            )

            Text(
                text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
                style = EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textOnBackground,
            )

            Text(
                text = schedule.toRelativeDayDescription(),
                style = EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textOnBackground,
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
