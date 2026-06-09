package com.tgyuu.analytics

sealed interface AnalyticsEvent {
    data class View(
        val screenName: String,
        val properties: Map<String, Any?>? = null,
    ) : AnalyticsEvent

    data class Click(
        val screenName: String,
        val buttonName: String,
        val properties: Map<String, Any?>? = null,
    ) : AnalyticsEvent

    data class Action(
        val screenName: String,
        val actionName: String,
        val actionResult: String? = null,
        val properties: Map<String, Any?>? = null,
    ) : AnalyticsEvent
}
