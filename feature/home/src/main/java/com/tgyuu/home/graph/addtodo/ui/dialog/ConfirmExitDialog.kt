package com.tgyuu.home.graph.addtodo.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
                title = "작성 중인 일정이 사라져요!",
                subText = "지금 뒤로 가면 일정이 저장되지 않습니다.\n계속 이어서 작성해 보세요.",
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "작성 중단하기",
                rightButtonText = "이어서 작성하기",
                onLeftButtonClick = onExitClick,
                onRightButtonClick = onContinueClick,
            )
        },
        modifier = Modifier.semantics { contentDescription = "ConfirmExitDialog" },
    )
}
