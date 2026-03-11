package com.tgyuu.common.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openPlayStore() {
    try {
        val playStoreIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(playStoreIntent)
    } catch (e: Exception) {
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(webIntent)
    }
}
