package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.domain.model.TodoSchedule

@Composable
internal fun ConfirmDelayDialog(
    schedule: TodoSchedule,
    onDismissRequest: () -> Unit,
    onDelayClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "${schedule.title} 일정을 하루 미루겠습니까?",
                subText = "미룬 일정은 수정하기에서 되돌릴 수 있습니다."
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
