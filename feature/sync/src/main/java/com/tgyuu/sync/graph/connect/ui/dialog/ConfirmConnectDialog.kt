package com.tgyuu.sync.graph.connect.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun ConfirmConnectDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "해당 ID로 연동할까요?",
                subText = buildAnnotatedString {
                    append("현재 기기에 있는 데이터는 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append("업로드된 데이터로 모두 대체")
                    }
                    append("됩니다.\n중요한 데이터는 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append("연동 전에 반드시 확인")
                    }
                    append("해주세요.")
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "연동",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
