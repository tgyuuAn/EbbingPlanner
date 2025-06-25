package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun PriorityContent(
    priority: String?,
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
        value = priority ?: "",
        onValueChange = onPriorityChange,
        hint = "얼마나 중요한 일정인가요?",
        keyboardType = KeyboardType.Number,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}
