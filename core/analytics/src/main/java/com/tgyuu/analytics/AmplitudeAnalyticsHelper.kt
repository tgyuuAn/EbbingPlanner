package com.tgyuu.analytics

import com.amplitude.android.Amplitude
import com.amplitude.core.events.BaseEvent
import com.tgyuu.deviceinfo.DeviceInfoProvider
import javax.inject.Inject

class AmplitudeAnalyticsHelper @Inject constructor(
    private val amplitude: Amplitude,
    private val deviceInfoProvider: DeviceInfoProvider,
) : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) {
        amplitude.track(event = event.toAmplitudeEvent())
    }

    override fun setUserId(userId: String?) {
        val androidUserId = "android-$userId"
        amplitude.setUserId(androidUserId)
    }

    private fun AnalyticsEvent.toAmplitudeEvent(): BaseEvent = BaseEvent().apply {
        val deviceType = deviceInfoProvider.getDeviceType()

        when (val event = this@toAmplitudeEvent) {
            is AnalyticsEvent.View -> {
                eventType = "View_${event.screenName}"
                eventProperties = event.properties.withDeviceType(deviceType).toMutableMap()
            }

            is AnalyticsEvent.Click -> {
                eventType = "Click_${event.buttonName}_${event.screenName}"
                eventProperties = event.properties.withDeviceType(deviceType).toMutableMap()
            }

            is AnalyticsEvent.Action -> {
                eventType = buildString {
                    append("Action_${event.actionName}_${event.screenName}")
                    event.actionResult?.let { append("_$it") }
                }
                eventProperties = event.properties.withDeviceType(deviceType).toMutableMap()
            }
        }
    }
}
