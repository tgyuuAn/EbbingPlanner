package com.tgyuu.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.R
import com.tgyuu.common.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

@Composable
fun LocalDate.toRelativeDayLabel(referenceDate: LocalDate = LocalDate.now()): String {
    val diff = referenceDate.daysUntil(this).toLong()
    return when {
        diff == 0L -> stringResource(R.string.date_today)
        diff > 0L -> stringResource(R.string.date_days_after, diff)
        else -> stringResource(R.string.date_days_before, -diff)
    }
}
