package com.tgyuu.home.graph.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogIconTop

@Composable
internal fun ConfirmExitDialog(
    onContinueClick: () -> Unit,
    onExitClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = { onContinueClick() },
        dialogTop = {
            EbbingDialogIconTop(
                iconId = com.tgyuu.designsystem.R.drawable.ic_notice,
                title = stringResource(R.string.home_exit_confirm_title),
                subText = stringResource(R.string.home_exit_confirm_sub),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.home_exit_confirm_stop),
                rightButtonText = stringResource(R.string.home_exit_confirm_continue),
                onLeftButtonClick = onExitClick,
                onRightButtonClick = onContinueClick,
            )
        },
        modifier = Modifier.semantics { contentDescription = "ConfirmExitDialog" },
    )
}
