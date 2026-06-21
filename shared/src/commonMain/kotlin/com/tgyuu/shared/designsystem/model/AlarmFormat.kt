package com.tgyuu.shared.designsystem.model

import androidx.compose.runtime.Composable
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.alarm_time_format
import ebbingplanner.shared.generated.resources.ds_am
import ebbingplanner.shared.generated.resources.ds_pm
import org.jetbrains.compose.resources.stringResource

/** 알람 시간 표시 (예: "오후 6시 30분") 다국어 포맷 */
@Composable
fun alarmTimeText(hour: Int, minute: Int): String {
    val period = if (hour < 12) stringResource(Res.string.ds_am) else stringResource(Res.string.ds_pm)
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    return stringResource(Res.string.alarm_time_format, period, displayHour, minute)
}
