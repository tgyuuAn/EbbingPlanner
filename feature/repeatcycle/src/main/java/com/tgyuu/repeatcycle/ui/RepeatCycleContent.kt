package com.tgyuu.repeatcycle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingChip
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.DayOfWeek

@Composable
internal fun RepeatCycleContent(
    repeatCycle: String,
    preview: List<Int>,
    onRepeatCycleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = repeatCycle,
        hint = "어떤 주기로 일정을 반복할까요?",
        keyboardType = KeyboardType.Number,
        onValueChange = onRepeatCycleChange,
        limit = 100,
        rightComponent = {
            if (repeatCycle.isNotEmpty()) {
                Image(
                    painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_delete_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onRepeatCycleChange("") },
                )
            }
        },
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )

    Text(
        text = "' , '를 기준으로 숫자를 분리해주세요.\n당일을 포함하려면 0을 기입해주세요.\n ex) 0, 1, 3, 7, 15",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp)
            .fillMaxWidth(),
    )

    Text(
        text = "예상 반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    Text(
        text = preview.toString(),
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
internal fun RestDayContent(
    restDays: Set<DayOfWeek>,
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
