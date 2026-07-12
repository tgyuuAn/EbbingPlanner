package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

@Composable
internal fun ConfirmDeleteSingleDialog(
    schedule: TodoScheduleUiModel,
    analyticsHelper: AnalyticsHelper,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            val prefix = stringResource(
                R.string.home_delete_single_confirm_prefix,
                schedule.title.originalText,
            )
            val deleteWord = stringResource(R.string.home_delete)
            val suffix = stringResource(R.string.home_delete_confirm_suffix)
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append(prefix)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                        append(deleteWord)
                    }
                    append(suffix)
                },
                subText = stringResource(R.string.home_delete_single_sub)
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.home_back),
                rightButtonText = stringResource(R.string.home_delete),
                onLeftButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "DeleteSingleDialog",
                            buttonName = "Back",
                        )
                    )
                    onDismissRequest()
                },
                onRightButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "DeleteSingleDialog",
                            buttonName = "Delete",
                        )
                    )
                    onDeleteClick()
                },
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
