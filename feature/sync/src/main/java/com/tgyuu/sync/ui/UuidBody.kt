package com.tgyuu.sync.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun UuidBody(
    uuid: String,
    onUuidClick: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current

    Text(
        text = "해당 디바이스의 고유 ID",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = uuid,
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.primaryDefault,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp)
            .clickable {
                clipboardManager.setText(AnnotatedString(uuid))
                onUuidClick()
            },
    )

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}
