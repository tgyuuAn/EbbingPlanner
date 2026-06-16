package com.tgyuu.tag.graph.main.ui.dialog

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
import com.tgyuu.designsystem.model.TodoTagUiModel

@Composable
internal fun DeleteDialog(
    tag: TodoTagUiModel,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val titlePrefix = stringResource(R.string.tag_delete_confirm_prefix, tag.name)
    val titleHighlight = stringResource(R.string.tag_delete)
    val titleSuffix = stringResource(R.string.tag_delete_confirm_suffix)

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(titlePrefix)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                        append(titleHighlight)
                    }
                    append(titleSuffix)
                },
                subText = stringResource(R.string.tag_delete_confirm_subtext)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.tag_back),
                rightButtonText = stringResource(R.string.tag_delete),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDeleteClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
