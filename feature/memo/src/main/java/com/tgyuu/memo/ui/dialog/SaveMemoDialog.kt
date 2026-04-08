package com.tgyuu.memo.ui.dialog

import androidx.compose.runtime.Composable
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
                title = "메모 저장 범위를 선택하세요",
                subText = "한 일정만 또는 관련 일정 전체에\n저장할 수 있어요"
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "선택한 일정만",
                rightButtonText = "관련 일정 모두 (${relatedCount}개)",
                onLeftButtonClick = onSaveToSingleClick,
                onRightButtonClick = onSaveToAllClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
