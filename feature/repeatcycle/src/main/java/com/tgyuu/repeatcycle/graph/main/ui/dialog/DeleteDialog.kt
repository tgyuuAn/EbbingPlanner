package com.tgyuu.repeatcycle.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val deleteDialogPrefix = stringResource(R.string.repeat_delete_dialog_prefix)
    val deleteDialogHighlight = stringResource(R.string.repeat_delete_dialog_highlight)
    val deleteDialogSuffix = stringResource(R.string.repeat_delete_dialog_suffix)

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(deleteDialogPrefix)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                        append(deleteDialogHighlight)
                    }
                    append(deleteDialogSuffix)
                },
                subText = stringResource(R.string.repeat_delete_dialog_sub_text)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.repeat_dialog_back),
                rightButtonText = stringResource(R.string.repeat_delete),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDeleteClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
