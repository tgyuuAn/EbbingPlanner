package com.tgyuu.analytics.data

import com.amplitude.android.Amplitude
import com.amplitude.core.events.BaseEvent
import com.tgyuu.analytics.domain.AnalyticsHelper
import com.tgyuu.analytics.domain.model.AnalyticsEvent
import javax.inject.Inject

class AmplitudeAnalyticsHelper @Inject constructor(
    private val amplitude: Amplitude,
) : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        amplitude.track(event = event.toAmplitudeEvent())
    }

    override fun setUserId(userId: String?) {
        val androidUserId = "android-$userId"
        amplitude.setUserId(androidUserId)
    }

    private fun AnalyticsEvent.toAmplitudeEvent(): BaseEvent = BaseEvent().apply {
        this.eventType = type
        this.eventProperties = properties
    }
}
