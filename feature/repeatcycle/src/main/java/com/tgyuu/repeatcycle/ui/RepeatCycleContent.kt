package com.tgyuu.repeatcycle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.RepeatCycle.Companion.DISPLAY_ERROR

@Composable
internal fun RepeatCycleContent(
    repeatCycle: String,
    onRepeatCycleChange: (String) -> Unit,
) {
    Text(
        text = stringResource(R.string.repeat_cycle_label),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = repeatCycle,
        hint = stringResource(R.string.repeat_cycle_input_hint),
        keyboardType = KeyboardType.Text,
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
        text = stringResource(R.string.repeat_cycle_input_guide),
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textDisabled,
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
        text = stringResource(R.string.repeat_cycle_preview_label),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Text(
        text = preview,
        style = EbbingTheme.typography.heading14SB,
        color = if (preview == DISPLAY_ERROR) EbbingTheme.colors.statusError else EbbingTheme.colors.textDisabled,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}
