package com.tgyuu.shared.ui.feature.home.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_back
import ebbingplanner.shared.generated.resources.home_delete
import ebbingplanner.shared.generated.resources.home_delete_confirm_suffix
import ebbingplanner.shared.generated.resources.home_delete_memo_confirm_prefix
import ebbingplanner.shared.generated.resources.home_delete_memo_sub
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConfirmDeleteMemoDialog(
    schedule: TodoScheduleUiModel,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            val deletePrefix = stringResource(Res.string.home_delete_memo_confirm_prefix, schedule.title)
            val deleteHighlight = stringResource(Res.string.home_delete)
            val deleteSuffix = stringResource(Res.string.home_delete_confirm_suffix)
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(deletePrefix)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(deleteHighlight)
                    }
                    append(deleteSuffix)
                },
                subText = stringResource(Res.string.home_delete_memo_sub)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.home_back),
                rightButtonText = stringResource(Res.string.home_delete),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDeleteClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
