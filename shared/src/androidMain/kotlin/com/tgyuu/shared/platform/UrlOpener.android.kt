package com.tgyuu.shared.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class UrlOpener(
    private val context: Context,
) {
    actual fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
