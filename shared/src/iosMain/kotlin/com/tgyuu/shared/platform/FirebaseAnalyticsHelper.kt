package com.tgyuu.shared.platform

/**
 * iOS Firebase Analytics helper.
 * Delegates to native Swift FirebaseAnalyticsBridge via lambda injection.
 */
class FirebaseAnalyticsHelper(
    private val onLogEvent: (String, Map<String, Any?>) -> Unit = { _, _ -> },
    private val onSetUserId: (String?) -> Unit = {},
) : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        onLogEvent(event.type, event.properties)
    }

    override fun setUserId(userId: String?) {
        onSetUserId(userId)
    }
}
