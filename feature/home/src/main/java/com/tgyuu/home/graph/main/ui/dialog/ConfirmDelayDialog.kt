package com.tgyuu.home.graph.main.ui.dialog

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun ConfirmDelayDialog(
    schedule: TodoScheduleUiModel,
    restDays: Set<DayOfWeek>,
    expectedDateExcludingRestDays: LocalDate?,
    expectedDateIncludingRestDays: LocalDate?,
    onDismissRequest: () -> Unit,
    onDelayClick: (includeRestDays: Boolean) -> Unit,
) {
    var excludeRestDays by remember { mutableStateOf(true) }

    val displayedExpectedDate = if (excludeRestDays) {
        expectedDateExcludingRestDays
    } else {
        expectedDateIncludingRestDays
    }

    val dateText = if (displayedExpectedDate != null) {
        "${schedule.date.monthValue}월 ${schedule.date.dayOfMonth}일(${
            schedule.date.dayOfWeek.getDisplayName(
                TextStyle.SHORT, Locale.KOREAN
            )
        }) → " + "${displayedExpectedDate.monthValue}월 ${displayedExpectedDate.dayOfMonth}일(${
            displayedExpectedDate.dayOfWeek.getDisplayName(
                TextStyle.SHORT, Locale.KOREAN
            )
        })"
    } else {
        ""
    }

    val restDaysText = if (restDays.isNotEmpty()) {
        val dayNames = restDays.sortedBy { it.value }
            .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, Locale.KOREAN) }
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
                text = "${schedule.title.originalText} 일정을 하루 미루시겠습니까?",
                color = EbbingTheme.colors.black,
                style = EbbingTheme.typography.headingMSB.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = dateText,
                color = EbbingTheme.colors.primaryDefault,
                style = EbbingTheme.typography.bodyMSB,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
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
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.dark2,
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
