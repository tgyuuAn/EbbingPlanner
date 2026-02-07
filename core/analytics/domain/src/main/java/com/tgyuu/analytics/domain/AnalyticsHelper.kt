package com.tgyuu.analytics.domain

import com.tgyuu.analytics.domain.model.AnalyticsEvent

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(userId: String?)
}
