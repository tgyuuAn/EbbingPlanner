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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
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
        val fromText = stringResource(
            R.string.home_delay_date_part,
            schedule.date.monthValue,
            schedule.date.dayOfMonth,
            schedule.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
        )
        val toText = stringResource(
            R.string.home_delay_date_part,
            displayedExpectedDate.monthValue,
            displayedExpectedDate.dayOfMonth,
            displayedExpectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
        )
        stringResource(R.string.home_delay_date_transition, fromText, toText)
    } else {
        ""
    }

    val restDaysText = if (restDays.isNotEmpty()) {
        val dayNames = restDays.sortedBy { it.value }
            .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, Locale.KOREAN) }
        stringResource(R.string.home_exclude_rest_days, dayNames)
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
                text = stringResource(
                    R.string.home_delay_single_confirm_title,
                    schedule.title.originalText,
                ),
                color = EbbingTheme.colors.textOnBackground,
                style = EbbingTheme.typography.heading20B.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = dateText,
                color = EbbingTheme.colors.primaryNormal,
                style = EbbingTheme.typography.body16M,
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
                            checkedColor = EbbingTheme.colors.primaryNormal,
                            uncheckedColor = EbbingTheme.colors.fillDisabled,
                        )
                    )

                    Text(
                        text = restDaysText,
                        style = EbbingTheme.typography.body16M,
                        color = EbbingTheme.colors.textOnBackground,
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = stringResource(R.string.home_delay_revert_notice),
                style = EbbingTheme.typography.caption14R,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.textDisabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.home_go_back),
                rightButtonText = stringResource(R.string.home_delay_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = { onDelayClick(!excludeRestDays) },
            )
        }
    }
}
