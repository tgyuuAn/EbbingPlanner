package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.R

@Composable
fun ConfirmSyncUpDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            val desc1 = stringResource(R.string.sync_confirm_sync_desc_1)
            val highlight1 = stringResource(R.string.sync_confirm_sync_desc_highlight_1)
            val desc2 = stringResource(R.string.sync_confirm_sync_desc_2)
            val highlight2 = stringResource(R.string.sync_confirm_sync_desc_highlight_2)
            val desc3 = stringResource(R.string.sync_confirm_sync_desc_3)

            EbbingDialogDefaultTop(
                title = stringResource(R.string.sync_confirm_sync_title),
                subText = buildAnnotatedString {
                    append(desc1)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.statusError)) {
                        append(highlight1)
                    }
                    append(desc2)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.statusError)) {
                        append(highlight2)
                    }
                    append(desc3)
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.sync_back),
                rightButtonText = stringResource(R.string.sync_sync),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
