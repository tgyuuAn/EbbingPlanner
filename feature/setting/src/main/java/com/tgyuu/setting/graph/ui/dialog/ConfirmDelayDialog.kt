package com.tgyuu.setting.graph.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun ConfirmClearDialog(
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append("데이터를 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                        append("초기화")
                    }
                    append(" 하시겠습니까?")
                },
                subText = "삭제한 데이터는 되돌릴 수 없습니다."
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "초기화",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onClearClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
