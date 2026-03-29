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
                title = "메모 저장 방법을 선택해주세요",
                subText = "관련 일정에 모두 추가하거나\n선택한 일정에만 추가할 수 있습니다."
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "선택한 일정에만",
                rightButtonText = "관련 일정 ${relatedCount}개 모두",
                onLeftButtonClick = onSaveToSingleClick,
                onRightButtonClick = onSaveToAllClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
