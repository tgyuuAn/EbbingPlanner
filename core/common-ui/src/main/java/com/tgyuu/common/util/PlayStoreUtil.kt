package com.tgyuu.common.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri

fun Context.openPlayStore() {
    val packageName = packageName
    try {
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(playStoreIntent)
    } catch (e: Exception) {
        Log.w(TAG, "Failed to open Play Store app, falling back to web", e)
        try {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(webIntent)
        } catch (webException: Exception) {
            Log.e(TAG, "Failed to open Play Store via web browser", webException)
        }
    }
}

private const val TAG = "PlayStoreUtil"
