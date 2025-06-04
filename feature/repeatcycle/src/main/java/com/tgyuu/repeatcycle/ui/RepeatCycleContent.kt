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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.InputState
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingChip
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.DayOfWeek

@Composable
internal fun RepeatCycleContent(
    repeatCycle: String,
    repeatCycleInputState: InputState,
    onRepeatCycleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSaveFailed = repeatCycleInputState == InputState.WARNING

    Text(
        text = "반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = repeatCycle,
        hint = "어떤 메모를 남겨둘까요?",
        keyboardType = KeyboardType.Text,
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

    EbbingVisibleAnimation(visible = isSaveFailed) {
        Text(
            text = "필수 항목을 입력해 주세요.",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.error,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
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
