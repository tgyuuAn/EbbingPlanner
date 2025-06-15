package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun ConfirmSyncUpDialog(
    onDismissRequest: () -> Unit,
    onAcceptClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "데이터를 동기화 할까요?",
                subText = buildAnnotatedString {
                    append("서로 다른 기기에서 수정된 데이터가 있을 경우, ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append("더 늦게 업데이트된 쪽으로 반영")
                    }
                    append("됩니다. 중요한 데이터는 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append("동기화 전에 반드시 확인")
                    }
                    append("해주세요.")
                },
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "동기화",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onAcceptClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
