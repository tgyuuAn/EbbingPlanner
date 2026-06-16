package com.tgyuu.repeatcycle.util

import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R

internal fun parsingIntervals(intervals: String): Result<List<Int>> = runCatching {
    intervals.split(",")
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filter { it.length < 4 }
        .map { it.toInt() }
        .distinct()
        .sorted()
        .toList()
}

fun List<Int>.toPreviewIntervals(resourceProvider: ResourceProvider): String {
    if (isEmpty()) return resourceProvider.getString(R.string.repeat_display_error)

    return when {
        this.size == 1 && this.first() == 0 -> resourceProvider.getString(R.string.repeat_interval_same_day_only)
        else -> this.joinToString(", ") { day ->
            if (day == 0) {
                resourceProvider.getString(R.string.repeat_interval_same_day)
            } else {
                resourceProvider.getString(R.string.repeat_interval_day, day)
            }
        }
    }
}
