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
                title = "데이터를 업데이트 할까요?",
                subText = buildAnnotatedString {
                    append("서버에 저장된 기존 데이터는 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append("모두 삭제되고")
                    }
                    append(" 이 기기의 데이터로 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append("덮어쓰기")
                    }
                    append("됩니다.\n이 작업은 되돌릴 수 없습니다.")
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
