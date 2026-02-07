package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingTextInputDropDown
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.RepeatCycleUiModel

@Composable
internal fun RepeatCycleContent(
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
