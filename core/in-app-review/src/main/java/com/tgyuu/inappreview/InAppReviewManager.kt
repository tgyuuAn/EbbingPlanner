package com.tgyuu.inappreview

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.tgyuu.common.util.openPlayStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppReviewManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    suspend fun requestAndLaunchReview(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReviewFlow().await()
            reviewManager.launchReviewFlow(activity, reviewInfo)
        } catch (_: Exception) {
            context.openPlayStore()
        }
    }

    companion object {
        private const val TAG = "InAppReviewManager"
    }
}
