package com.tgyuu.inappreview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class InAppReviewManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(context)

    // Fallback으로 PlayStore 열기
    suspend fun requestAndLaunchReview(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReviewFlow().await()
            reviewManager.launchReviewFlow(activity, reviewInfo)
        } catch (_: Exception) {
            openPlayStoreForReview()
        }
    }

    private fun openPlayStoreForReview() {
        try {
            val packageName = context.packageName
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(playStoreIntent)
        } catch (e: Exception) {
            val packageName = context.packageName
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
