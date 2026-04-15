package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.TrackScreenViewEvent
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingSolidButton

@Composable
internal fun InAppReviewDialog(
    onDismiss: () -> Unit,
    onReviewClick: () -> Unit,
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    TrackScreenViewEvent(key = Unit, screenName = "ReviewDialog")

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "에빙플래너가 도움이 되었나요?",
                subText = "남겨주신 리뷰를 바탕으로 \n더 편리한 서비스를 만들겠습니다.",
            )
        },
        dialogBottom = {
            EbbingSolidButton(
                label = "리뷰 작성하기",
                onClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(
                            screenName = "ReviewDialog",
                            buttonName = "Review",
                        )
                    )
                    onReviewClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            )
        },
        onDismissRequest = onDismiss,
    )
}
