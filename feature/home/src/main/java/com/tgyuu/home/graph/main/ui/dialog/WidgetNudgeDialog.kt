package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun WidgetNudgeDialog(
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp)
                    .size(32.dp)
                    .align(Alignment.End)
                    .clickable { onDismiss() },
            )
            Text(
                text = "첫 일정 등록 완료!",
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Text(
                text = "에빙플래너 위젯으로도\n일정을 간편하게 체크해보세요.",
                textAlign = TextAlign.Center,
                style = EbbingTheme.typography.headingSSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 24.dp)
                    .padding(horizontal = 16.dp),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .wrapContentHeight()
                    .background(color = EbbingTheme.colors.light3),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_widget_nudge),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 23.5.dp)
                        .size(173.dp, 121.dp)
                )
            }
        }
    }
}
