package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun PriorityContent(
    priority: String?,
    onPriorityChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.home_priority),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = priority ?: "",
        onValueChange = onPriorityChange,
        hint = stringResource(R.string.home_priority_hint),
        keyboardType = KeyboardType.Number,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}
