package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.sync.R

@Composable
fun CameraPermissionDialog(
    shouldShowRationale: Boolean,
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.sync_camera_permission_title),
                subText = if (shouldShowRationale) {
                    stringResource(R.string.sync_camera_permission_rationale_settings)
                } else {
                    stringResource(R.string.sync_camera_permission_rationale)
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.sync_cancel),
                rightButtonText = if (shouldShowRationale) {
                    stringResource(R.string.sync_go_settings)
                } else {
                    stringResource(R.string.sync_allow)
                },
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
