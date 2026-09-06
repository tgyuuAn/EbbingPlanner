package com.tgyuu.shared.platform

data class AnalyticsEvent(
    val type: String,
    val properties: Map<String, Any?> = emptyMap(),
) {
    object Types {
        const val SCREEN_VIEW = "screen_view"
        const val BUTTON_CLICK = "button_click"
        const val ACTION = "action"
    }

    object PropertiesKeys {
        const val SCREEN_NAME = "screen_name"
        const val ACTION_NAME = "action_name"
        const val BUTTON_NAME = "button_name"
    }
}

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(userId: String?)
}

/** Android AnalyticsEvent.Click(screenName, buttonName) 대응 단축 확장 (nullable-safe). */
fun AnalyticsHelper?.logClick(screenName: String, buttonName: String) {
    this?.logEvent(
        AnalyticsEvent(
            type = AnalyticsEvent.Types.BUTTON_CLICK,
            properties = mapOf(
                AnalyticsEvent.PropertiesKeys.SCREEN_NAME to screenName,
                AnalyticsEvent.PropertiesKeys.BUTTON_NAME to buttonName,
            ),
        )
    )
}

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {}
    override fun setUserId(userId: String?) {}
}

class DebugAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        println("Analytics: ${event.type} - ${event.properties}")
    }

    override fun setUserId(userId: String?) {
        println("Analytics: setUserId($userId)")
    }
}
