package com.tgyuu.shared.designsystem.model

import androidx.compose.runtime.Composable
import com.tgyuu.shared.domain.model.RepeatCycle
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.repeat_display_daily
import ebbingplanner.shared.generated.resources.repeat_display_daily_days
import ebbingplanner.shared.generated.resources.repeat_display_error
import ebbingplanner.shared.generated.resources.repeat_interval_day
import ebbingplanner.shared.generated.resources.repeat_interval_same_day
import ebbingplanner.shared.generated.resources.repeat_interval_same_day_only
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** ViewModel 등 비-Composable(suspend) 컨텍스트에서 반복 주기 표시명 생성 */
suspend fun RepeatCycle.toDisplayName(): String {
    if (intervals.isEmpty()) return getString(Res.string.repeat_display_error)
    return when {
        id == RepeatCycle.DAILY_REPEAT_ID && intervals.size <= 1 -> getString(Res.string.repeat_display_daily)
        id == RepeatCycle.DAILY_REPEAT_ID -> getString(Res.string.repeat_display_daily_days, intervals.size)
        intervals.size == 1 && intervals.first() == 0 -> getString(Res.string.repeat_interval_same_day_only)
        else -> buildList {
            for (day in intervals) {
                add(
                    if (day == 0) getString(Res.string.repeat_interval_same_day)
                    else getString(Res.string.repeat_interval_day, day),
                )
            }
        }.joinToString(", ")
    }
}

/** 반복 주기 입력 화면의 미리보기 표시 (Composable) */
@Composable
fun List<Int>.toRepeatPreviewDisplay(): String {
    if (isEmpty()) return stringResource(Res.string.repeat_display_error)
    return when {
        size == 1 && first() == 0 -> stringResource(Res.string.repeat_interval_same_day_only)
        else -> buildList {
            for (day in this@toRepeatPreviewDisplay) {
                add(
                    if (day == 0) stringResource(Res.string.repeat_interval_same_day)
                    else stringResource(Res.string.repeat_interval_day, day),
                )
            }
        }.joinToString(", ")
    }
}
