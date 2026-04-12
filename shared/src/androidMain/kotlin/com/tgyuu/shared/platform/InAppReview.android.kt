package com.tgyuu.shared.platform

import android.app.Activity

actual class InAppReviewManager(private val activity: Activity?) {
    actual fun requestReview() {
        // TODO: Implement with Google Play In-App Review API
        // val manager = ReviewManagerFactory.create(activity)
        // val request = manager.requestReviewFlow()
    }
}
