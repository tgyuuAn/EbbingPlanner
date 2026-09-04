package com.tgyuu.inappreview

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tgyuu.common.util.openPlayStore
import kotlinx.coroutines.tasks.await


class InAppReviewManager(
    private val context: Context,
) {
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    suspend fun requestInAppReview(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReviewFlow().await()
            reviewManager.launchReviewFlow(activity, reviewInfo)
        } catch (_: Exception) {
            context.openPlayStore()
        }
    }

    fun openPlayStoreForReview() {
        context.openPlayStore()
    }
}
