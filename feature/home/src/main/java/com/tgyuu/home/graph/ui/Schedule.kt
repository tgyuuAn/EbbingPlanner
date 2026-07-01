package com.tgyuu.home.graph.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.toFormattedString
import com.tgyuu.designsystem.util.toRelativeDayLabel
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.toShortLabel
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.LocalDate

@Composable
internal fun ScheduleContent(
    schedules: List<LocalDate>,
    modifier: Modifier = Modifier,
) {
    EbbingVisibleAnimation(schedules.isNotEmpty()) {
        val shape = RoundedCornerShape(12.dp)
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .clip(shape)
                .border(width = 1.dp, color = EbbingTheme.colors.strokeOutline, shape = shape)
                .background(EbbingTheme.colors.fillNormal)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.home_study_schedule_count, schedules.size),
                style = EbbingTheme.typography.heading16SB,
                color = EbbingTheme.colors.textOnBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )

            schedules.forEachIndexed { idx, item ->
                ScheduleCard(
                    idx = idx + 1,
                    schedule = item,
                    showDivider = idx < schedules.lastIndex,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    idx: Int,
    schedule: LocalDate,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(EbbingTheme.colors.fillTextfield),
                ) {
                    Text(
                        text = idx.toString(),
                        style = EbbingTheme.typography.caption12R,
                        color = EbbingTheme.colors.textSub,
                    )
                }

                Text(
                    text = stringResource(
                        R.string.home_schedule_date_day,
                        schedule.toFormattedString(),
                        schedule.dayOfWeek.toShortLabel(),
                    ),
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textOnBackground,
                )
            }

            Text(
                text = schedule.toRelativeDayLabel(),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textSub,
            )
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(EbbingTheme.colors.strokeOutline),
            )
        }
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
                text = stringResource(R.string.home_study_schedule_count, schedules.size),
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
                text = stringResource(
                R.string.home_schedule_date_day,
                schedule.toFormattedString(),
                schedule.dayOfWeek.toShortLabel(),
            ),
                style = EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textOnBackground,
            )

            Text(
                text = schedule.toRelativeDayLabel(),
                style = EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textOnBackground,
            )
        }

        EbbingCheck(
            checked = isChecked,
            colorValue = colorValue,
            onCheckedChange = { onCheckSchedule(idx) },
            modifier = Modifier.padding(start = 12.dp).size(20.dp),
        )
    }
}
