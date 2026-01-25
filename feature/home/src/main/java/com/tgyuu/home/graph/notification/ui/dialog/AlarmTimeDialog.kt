package com.tgyuu.home.graph.notification.ui.dialog

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
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.picker.EbbingPicker

@Composable
internal fun AlarmTimeDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismissRequest: () -> Unit,
    onConfirmClick: (Int, Int) -> Unit,
) {
    var newAmPm by remember { mutableStateOf(if (initialHour >= 12) "오후" else "오전") }
    var newHour by remember { mutableStateOf(initialHour) }
    var newMinute by remember { mutableStateOf(initialMinute) }

    val pickerInitialAmPm = if (initialHour >= 12) "오후" else "오전"
    val pickerInitialHour = when {
        initialHour == 0 -> "12"
        initialHour > 12 -> (initialHour - 12).toString()
        initialHour == 12 -> "12"
        else -> initialHour.toString()
    }
    val pickerInitialMinute = initialMinute.toString().padStart(2, '0')

    EbbingDialog(
        onDismissRequest = onDismissRequest,
        dialogTop = {
            EbbingDialogDefaultTop(
                title = "알림 시간",
                subText = "언제 남은 일정 알림을 보낼까요?",
            )
        },
        dialogBottom = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
            ) {
                EbbingPicker(
                    initialAmPm = pickerInitialAmPm,
                    initialHour = pickerInitialHour,
                    initialMinute = pickerInitialMinute,
                    onValueChange = { amPm, hour, minute ->
                        newAmPm = amPm
                        newHour = hour
                        newMinute = minute
                    },
                    modifier = Modifier.padding(vertical = 20.dp),
                )

                EbbingSolidButton(
                    label = "적용하기",
                    onClick = {
                        val adjustedHour = when {
                            newAmPm == "오후" && newHour == 12 -> 12
                            newAmPm == "오후" -> newHour + 12
                            newAmPm == "오전" && newHour == 12 -> 0
                            else -> newHour
                        }
                        onConfirmClick(adjustedHour, newMinute)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
