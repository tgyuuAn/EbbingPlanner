package com.tgyuu.memo.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop

@Composable
internal fun SaveMemoDialog(
    relatedCount: Int,
    onDismissRequest: () -> Unit,
    onSaveToAllClick: () -> Unit,
    onSaveToSingleClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.memo_save_scope_title),
                subText = stringResource(R.string.memo_save_scope_subtext)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.memo_save_scope_single),
                rightButtonText = stringResource(R.string.memo_save_scope_all, relatedCount),
                onLeftButtonClick = onSaveToSingleClick,
                onRightButtonClick = onSaveToAllClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
