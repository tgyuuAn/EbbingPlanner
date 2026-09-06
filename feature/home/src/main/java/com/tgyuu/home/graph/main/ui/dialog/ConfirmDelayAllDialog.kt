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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.common.getDisplayName
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun ConfirmDelayAllDialog(
    schedule: TodoScheduleUiModel,
    restDays: Set<DayOfWeek>,
    onDismissRequest: () -> Unit,
    onDelayClick: (includeRestDays: Boolean) -> Unit,
) {
    var excludeRestDays by remember { mutableStateOf(true) }
    val restDaysText = if (restDays.isNotEmpty()) {
        val dayNames = restDays.sortedBy { it.isoDayNumber }
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
                    R.string.home_delay_all_confirm_title,
                    schedule.title.originalText,
                ),
                color = EbbingTheme.colors.textOnBackground,
                style = EbbingTheme.typography.heading20B,
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
                color = EbbingTheme.colors.textDisabled,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(R.string.home_go_back),
                rightButtonText = stringResource(R.string.home_delay_button),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = { onDelayClick(false) },
            )
        }
    }
}
