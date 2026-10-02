package com.tgyuu.shared.platform

import platform.StoreKit.SKStoreReviewController

actual class InAppReviewManager {
    actual fun requestReview() {
        SKStoreReviewController.requestReview()
    }
}
