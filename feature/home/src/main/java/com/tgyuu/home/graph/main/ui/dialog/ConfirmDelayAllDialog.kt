package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

@Composable
internal fun ConfirmDelayAllDialog(
    schedule: TodoScheduleUiModel,
    onDismissRequest: () -> Unit,
    onDelayClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append("${schedule.title.originalText} 와 연계된 ${schedule.date.monthValue}월 ${schedule.date.dayOfMonth}일 이후 일정을 모두 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append("미루기")
                    }
                    append(" 하시겠습니까?")
                },
                subText = "미룬 일정들은 수정하기에서 되돌릴 수 있습니다."
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "미루기",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDelayClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
