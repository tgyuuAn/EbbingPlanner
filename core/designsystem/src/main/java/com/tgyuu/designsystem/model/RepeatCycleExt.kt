package com.tgyuu.designsystem.model

import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.model.RepeatCycle

fun RepeatCycle.toDisplayName(resourceProvider: ResourceProvider): String {
    if (intervals.isEmpty()) return resourceProvider.getString(R.string.repeat_display_error)
    return when {
        id == RepeatCycle.DAILY_REPEAT_ID && intervals.size <= 1 -> resourceProvider.getString(R.string.repeat_display_daily)
        id == RepeatCycle.DAILY_REPEAT_ID -> resourceProvider.getString(R.string.repeat_display_daily_days, intervals.size)
        intervals.size == 1 && intervals.first() == 0 -> resourceProvider.getString(R.string.repeat_interval_same_day_only)
        else -> intervals.joinToString(", ") { day ->
            if (day == 0) resourceProvider.getString(R.string.repeat_interval_same_day)
            else resourceProvider.getString(R.string.repeat_interval_day, day)
        }
    }
}
