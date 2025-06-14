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
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.ZonedDateTime

@Composable
internal fun UuidBody(
    uuid: String,
    lastSyncedAt: ZonedDateTime?,
    lastUpdatedAt: ZonedDateTime?,
    onUuidClick: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current

    Text(
        text = "해당 디바이스의 고유 ID :",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Text(
        text = uuid,
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.primaryDefault,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                clipboardManager.setText(AnnotatedString(uuid))
                onUuidClick()
            },
    )

    Text(
        text = "해당 기기의 마지막 업데이트 시점 : ",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    )

    Text(
        text = lastSyncedAt?.toLocalDateTime()?.toFormattedString() ?: "기록 없음",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.primaryDefault,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                clipboardManager.setText(AnnotatedString(uuid))
                onUuidClick()
            },
    )

    Text(
        text = "서버에 저장된 해당 ID의 마지막 업데이트 시점 : ",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    )

    Text(
        text = lastUpdatedAt?.toLocalDateTime()?.toFormattedString() ?: "기록이 없거나 네트워크가 없음",
        style = EbbingTheme.typography.bodySR,
        color = EbbingTheme.colors.primaryDefault,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
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
