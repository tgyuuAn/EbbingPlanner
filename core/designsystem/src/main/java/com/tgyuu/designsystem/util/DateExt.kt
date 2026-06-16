package com.tgyuu.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun LocalDate.toRelativeDayLabel(referenceDate: LocalDate = LocalDate.now()): String {
    val diff = ChronoUnit.DAYS.between(referenceDate, this)
    return when {
        diff == 0L -> stringResource(R.string.date_today)
        diff > 0L -> stringResource(R.string.date_days_after, diff)
        else -> stringResource(R.string.date_days_before, -diff)
    }
}
