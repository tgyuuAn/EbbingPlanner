package com.tgyuu.analytics.data

import com.tgyuu.analytics.domain.AnalyticsHelper
import com.tgyuu.analytics.domain.model.AnalyticsEvent

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun setUserId(userId: String?) = Unit
}
