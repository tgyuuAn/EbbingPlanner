package com.tgyuu.shared.ui.feature.home.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.calendar.toKorean
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.datetime.DayOfWeek

@Composable
internal fun ConfirmDelayAllDialog(
    schedule: TodoScheduleUiModel,
    restDays: Set<DayOfWeek>,
    onDismissRequest: () -> Unit,
    onDelayClick: (includeRestDays: Boolean) -> Unit,
) {
    var excludeRestDays by remember { mutableStateOf(true) }
    val restDaysText = if (restDays.isNotEmpty()) {
        val dayNames = restDays.sortedBy { it.ordinal }
            .joinToString(", ") { it.toKorean() }
        "쉬는 요일 제외 ($dayNames)"
    } else {
        ""
    }

    EbbingDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = "모든 ${schedule.title} 일정을\n하루씩 미루시겠습니까?",
                color = EbbingTheme.colors.black,
                style = EbbingTheme.typography.headingMSB,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 40.dp),
            )

            if (restDays.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = excludeRestDays,
                        onCheckedChange = { excludeRestDays = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EbbingTheme.colors.primaryDefault,
                            uncheckedColor = EbbingTheme.colors.light1,
                        )
                    )

                    Text(
                        text = restDaysText,
                        style = EbbingTheme.typography.bodyMR,
                        color = EbbingTheme.colors.black,
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "미룬 일정은 수정하기에서 다시 되돌릴 수 있습니다.",
                style = EbbingTheme.typography.bodySR,
                color = EbbingTheme.colors.dark2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            EbbingDialogBottom(
                leftButtonText = "뒤로가기",
                rightButtonText = "미루기",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = { onDelayClick(!excludeRestDays) },
            )
        }
    }
}
