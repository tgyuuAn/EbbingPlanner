package com.tgyuu.shared.ui.feature.home.addtodo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toRelativeDayDescription
import com.tgyuu.shared.designsystem.component.EbbingChip
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.component.EbbingTextInputDropDown
import com.tgyuu.shared.designsystem.component.calendar.toKorean
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
fun TitleContent(
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "제목",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = title,
        hint = "무엇을 학습하실건가요?",
        keyboardType = KeyboardType.Text,
        onValueChange = onTitleChange,
        limit = 100,
        rightComponent = {
            if (title.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "삭제",
                    tint = EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onTitleChange("") },
                )
            }
        },
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun TagContent(
    tag: TodoTagUiModel?,
    onTagDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "태그",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = tag?.name ?: "",
        color = tag?.color,
        onDropDownClick = onTagDropDownClick,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun PriorityContent(
    priority: String,
    onPriorityChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "우선순위",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = priority,
        onValueChange = onPriorityChange,
        hint = "얼마나 중요한 일정인가요?",
        keyboardType = KeyboardType.Number,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun RepeatCycleContent(
    repeatCycle: RepeatCycleUiModel?,
    onRepeatCycleDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = repeatCycle?.displayName ?: "",
        onDropDownClick = onRepeatCycleDropDownClick,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun RestDayContent(
    restDays: ImmutableSet<DayOfWeek>,
    onRestDayChange: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "쉬는 날",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        DayOfWeek.entries.forEach {
            EbbingChip(
                label = it.toKorean(),
                selected = it in restDays,
                onChipClicked = { onRestDayChange(it) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ScheduleContent(
    schedules: List<LocalDate>,
    modifier: Modifier = Modifier,
) {
    if (schedules.isEmpty()) return

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
