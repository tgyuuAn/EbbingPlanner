package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

@Composable
internal fun ConfirmDeleteRemainingDialog(
    schedule: TodoScheduleUiModel,
    analyticsHelper: AnalyticsHelper,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append("${schedule.title.originalText} 와 연계된 ${schedule.date.monthValue}월 ${schedule.date.dayOfMonth}일 이후 일정을 모두 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                        append("삭제")
                    }
                    append(" 하시겠습니까?")
                },
                subText = "삭제한 일정들은 되돌릴 수 없으니 신중히 선택해 주세요."
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "삭제",
                onLeftButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "DeleteAllDialog",
                            buttonName = "Back",
                        )
                    )
                    onDismissRequest()
                },
                onRightButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "DeleteAllDialog",
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
