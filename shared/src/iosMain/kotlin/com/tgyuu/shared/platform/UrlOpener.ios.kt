package com.tgyuu.shared.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class UrlOpener {
    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}
