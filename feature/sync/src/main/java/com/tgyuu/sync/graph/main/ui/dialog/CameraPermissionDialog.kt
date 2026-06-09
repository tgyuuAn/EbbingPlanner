package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop

@Composable
fun CameraPermissionDialog(
    shouldShowRationale: Boolean,
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "카메라 권한 필요",
                subText = if (shouldShowRationale) {
                    "QR 코드를 스캔하려면 설정에서 카메라 권한을 허용해주세요."
                } else {
                    "QR 코드를 스캔하려면 카메라 권한이 필요합니다."
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "취소",
                rightButtonText = if (shouldShowRationale) "설정으로 이동" else "허용",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
