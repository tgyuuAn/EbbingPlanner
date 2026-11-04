package com.tgyuu.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf

abstract class AnalyticsHelper {
    abstract fun logEvent(event: AnalyticsEvent)
    abstract fun setUserId(userId: String?)
}

class NoOpAnalyticsHelper : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun setUserId(userId: String?) = Unit
}

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}

@Composable
fun TrackScreenViewEvent(
    key: Any?,
    screenName: String?,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = LaunchedEffect(key) {
    if (screenName != null) {
        analyticsHelper.logEvent(AnalyticsEvent.View(screenName = screenName))
    }
}

@Composable
fun TrackClickEvent(
    key: Any?,
    screenName: String,
    buttonName: String,
    properties: Map<String, Any?>? = null,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = LaunchedEffect(key) {
    analyticsHelper.logEvent(
        AnalyticsEvent.Click(
            screenName = screenName,
            buttonName = buttonName,
            properties = properties,
        )
    )
}
