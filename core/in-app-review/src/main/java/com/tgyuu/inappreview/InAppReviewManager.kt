package com.tgyuu.inappreview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
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
            reviewManager.launchReviewFlow(activity, reviewInfo).await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to launch in-app review, falling back to Play Store", e)
            openPlayStoreForReview()
        }
    }

    fun openPlayStoreForReview() {
        val packageName = context.packageName
        try {
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                "market://details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(playStoreIntent)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to open Play Store app, falling back to web", e)
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    companion object {
        private const val TAG = "InAppReviewManager"
    }
}
