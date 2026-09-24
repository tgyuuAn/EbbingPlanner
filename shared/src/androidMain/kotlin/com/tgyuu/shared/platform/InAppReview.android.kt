package com.tgyuu.shared.platform

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

actual class InAppReviewManager(private val activity: Activity?) {
    actual fun requestReview() {
        val activity = activity ?: return
        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(activity, task.result)
            }
        }
    }
}
