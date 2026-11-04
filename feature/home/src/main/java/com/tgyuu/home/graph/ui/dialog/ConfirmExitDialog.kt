package com.tgyuu.home.graph.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.analytics.TrackScreenViewEvent
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogIconTop

private const val SCREEN_NAME = "ConfirmExitDialog"

/**
 * 작성 중에 나가려 할 때 띄우는 확인 다이얼로그.
 * 작성하다 포기한 이탈을 따로 볼 수 있게, 노출과 선택을 로그로 남겨요.
 *
 * @param logProperties 로그에 함께 남길 속성 (실험군 등)
 */
@Composable
internal fun ConfirmExitDialog(
    onContinueClick: () -> Unit,
    onExitClick: () -> Unit,
    logProperties: Map<String, Any?>? = null,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    TrackScreenViewEvent(key = Unit, screenName = SCREEN_NAME, properties = logProperties)

    EbbingDialog(
        onDismissRequest = {
            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = SCREEN_NAME,
                    actionName = "dismiss",
                    properties = logProperties,
                )
            )
            onContinueClick()
        },
        dialogTop = {
            EbbingDialogIconTop(
                iconId = com.tgyuu.designsystem.R.drawable.ic_notice,
                title = stringResource(R.string.home_exit_confirm_title),
                subText = stringResource(R.string.home_exit_confirm_sub),
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.home_exit_confirm_stop),
                rightButtonText = stringResource(R.string.home_exit_confirm_continue),
                onLeftButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Exit", properties = logProperties)
                    )
                    onExitClick()
                },
                onRightButtonClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Continue", properties = logProperties)
                    )
                    onContinueClick()
                },
            )
        },
        modifier = Modifier.semantics { contentDescription = "ConfirmExitDialog" },
    )
}
