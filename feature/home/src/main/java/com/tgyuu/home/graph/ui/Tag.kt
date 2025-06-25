package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingTextInputDropDown
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoTag

@Composable
internal fun TagContent(
    tag: TodoTag?,
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
