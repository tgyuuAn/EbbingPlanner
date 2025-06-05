package com.tgyuu.repeatcycle.ui

import androidx.compose.foundation.Image
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
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.RepeatCycle.Companion.DISPLAY_ERROR

@Composable
internal fun RepeatCycleContent(
    repeatCycle: String,
    onRepeatCycleChange: (String) -> Unit,
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
        limit = 60,
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
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )

    Text(
        text = "' , '를 기준으로 숫자를 분리해주세요\n당일을 포함하려면 0을 기입해주세요\n1000 미만의 숫자만 입력하실 수 있습니다.\n ex) 0, 1, 3, 7, 15",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 8.dp, start = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
internal fun PreviewContent(
    preview: String,
) {
    Text(
        text = "예상 반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    Text(
        text = preview,
        style = EbbingTheme.typography.bodySSB,
        color = if (preview == DISPLAY_ERROR) EbbingTheme.colors.error else EbbingTheme.colors.dark2,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}
