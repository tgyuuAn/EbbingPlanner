package com.tgyuu.setting.graph.ui.dialog

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
internal fun ConfirmClearDialog(
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
) {
    val clearDialogPrefix = stringResource(R.string.setting_clear_dialog_title_prefix)
    val clearDialogHighlight = stringResource(R.string.setting_clear_dialog_title_highlight)
    val clearDialogSuffix = stringResource(R.string.setting_clear_dialog_title_suffix)

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(clearDialogPrefix)
                    withStyle(SpanStyle(color = EbbingTheme.colors.statusError)) {
                        append(clearDialogHighlight)
                    }
                    append(clearDialogSuffix)
                },
                subText = stringResource(R.string.setting_clear_dialog_subtext)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.setting_back),
                rightButtonText = stringResource(R.string.setting_clear),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onClearClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
