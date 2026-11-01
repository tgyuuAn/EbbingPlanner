package com.tgyuu.analytics

import android.util.Log
import com.tgyuu.deviceinfo.DeviceInfoProvider
import javax.inject.Inject

class DebugAnalyticsHelper @Inject constructor(
    private val deviceInfoProvider: DeviceInfoProvider,
    private var userId: String = "",
) : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) {
        val deviceType = deviceInfoProvider.getDeviceType()

        val (eventType, rawProperties) = when (event) {
            is AnalyticsEvent.View -> "View_${event.screenName}" to event.properties
            is AnalyticsEvent.Click -> "Click_${event.buttonName}_${event.screenName}" to event.properties
            is AnalyticsEvent.Action -> buildString {
                append("Action_${event.actionName}_${event.screenName}")
                event.actionResult?.let { append("_$it") }
            } to event.properties
        }
        val properties = rawProperties.withDeviceType(deviceType)
        Log.d("DebugAnalyticsHelper", "userId=$userId, type=$eventType, properties=$properties")
    }

    override fun setUserId(userId: String?) {
        this.userId = userId ?: ""
        Log.d("DebugAnalyticsHelper", "setUserId 호출 : ${this.userId}")
    }
}
