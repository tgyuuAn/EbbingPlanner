package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.R

@Composable
fun ConfirmDisconnectDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.sync_confirm_disconnect_title),
                subText = stringResource(R.string.sync_confirm_disconnect_desc),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.sync_back),
                rightButtonText = stringResource(R.string.sync_disconnect),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
