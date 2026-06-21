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
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.datetime.DayOfWeek
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_delay_all_confirm_title
import ebbingplanner.shared.generated.resources.home_delay_button
import ebbingplanner.shared.generated.resources.home_delay_revert_notice
import ebbingplanner.shared.generated.resources.home_exclude_rest_days
import ebbingplanner.shared.generated.resources.home_go_back
import org.jetbrains.compose.resources.stringResource
import com.tgyuu.shared.designsystem.component.calendar.rememberDayOfWeekShortLabels

@Composable
internal fun ConfirmDelayAllDialog(
    schedule: TodoScheduleUiModel,
    restDays: Set<DayOfWeek>,
    onDismissRequest: () -> Unit,
    onDelayClick: (includeRestDays: Boolean) -> Unit,
) {
    var excludeRestDays by remember { mutableStateOf(true) }
    val dayLabels = rememberDayOfWeekShortLabels()
    val restDaysText = if (restDays.isNotEmpty()) {
        val dayNames = restDays.sortedBy { it.ordinal }
            .joinToString(", ") { dayLabels.getValue(it) }
        stringResource(Res.string.home_exclude_rest_days, dayNames)
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
                text = stringResource(Res.string.home_delay_all_confirm_title, schedule.title),
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
                text = stringResource(Res.string.home_delay_revert_notice),
                style = EbbingTheme.typography.bodySR,
                color = EbbingTheme.colors.dark2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.home_go_back),
                rightButtonText = stringResource(Res.string.home_delay_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = { onDelayClick(!excludeRestDays) },
            )
        }
    }
}
