package com.tgyuu.ebbingplanner.ui.update

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.ebbingplanner.R

@Composable
internal fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismissRequest: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    EbbingDialog(
        onDismissRequest = onDismissRequest,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = stringResource(R.string.update_title),
                subText = updateInfo.noticeMsg,
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "닫기",
                rightButtonText = "업데이트",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onUpdateClick,
            )
        },
    )
}
