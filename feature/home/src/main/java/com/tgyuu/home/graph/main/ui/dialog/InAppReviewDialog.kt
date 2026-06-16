package com.tgyuu.home.graph.main.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.TrackScreenViewEvent
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.designsystem.R
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
                title = stringResource(R.string.home_review_title),
                subText = stringResource(R.string.home_review_sub),
            )
        },
        dialogBottom = {
            EbbingSolidButton(
                label = stringResource(R.string.home_review_button),
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
