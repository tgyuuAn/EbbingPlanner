package com.tgyuu.setting.graph.main.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.picker.EbbingPicker

@Composable
internal fun AlarmTimeBottomSheet(
    originHour: String,
    originMinute: String,
    onUpdateClick: (String, String) -> Unit,
) {
    var newAmPm by remember { mutableStateOf(if (originHour.toInt() >= 12) "오후" else "오전") }
    var newHour by remember { mutableStateOf(originHour) }
    var newMinute by remember { mutableStateOf(originMinute) }

    val initialAmPm = if (originHour.toInt() >= 12) "오후" else "오전"
    val initialHour =
        if (originHour.toInt() >= 12) (originHour.toInt() - 12).toString() else originHour
    val initialMinute = originMinute

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = "알람 시간",
            subTitle = "언제 남은 일정 알림을 보낼까요?"
        )

        EbbingPicker(
            initialAmPm = initialAmPm,
            initialHour = initialHour,
            initialMinute = initialMinute,
            onValueChange = { amPm, hour, minute ->
                newAmPm = amPm
                newHour = hour.toString()
                newMinute = minute.toString()
            },
            modifier = Modifier.padding(vertical = 30.dp),
        )

        EbbingSolidButton(
            label = "적용하기",
            onClick = {
                var hour = if (newAmPm == "오후") (newHour.toInt() + 12).toString() else newHour
                if (hour == "24") {
                    hour = "0"
                }
                onUpdateClick(hour, newMinute)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
