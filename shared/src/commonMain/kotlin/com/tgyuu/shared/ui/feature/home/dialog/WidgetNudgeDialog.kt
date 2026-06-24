package com.tgyuu.shared.ui.feature.home.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_widget_nudge_label
import ebbingplanner.shared.generated.resources.home_widget_nudge_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_close

@Composable
internal fun WidgetNudgeDialog(
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = null,
                tint = EbbingTheme.colors.dark2,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp)
                    .size(32.dp)
                    .align(Alignment.End)
                    .clickable { onDismiss() },
            )
            Text(
                text = stringResource(Res.string.home_widget_nudge_label),
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Text(
                text = stringResource(Res.string.home_widget_nudge_title),
                textAlign = TextAlign.Center,
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 24.dp)
                    .padding(horizontal = 16.dp),
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(color = EbbingTheme.colors.light3)
                    .padding(vertical = 24.dp),
            ) {
                // Placeholder for widget preview image
                // In actual implementation, this would be replaced with actual widget preview
                Text(
                    text = "Widget Preview",
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark2,
                )
            }
        }
    }
}
