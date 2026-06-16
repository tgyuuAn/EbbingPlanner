package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun CalendarController(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onGotoTodayClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSyncButton: Boolean = true,
    onSyncClick: () -> Unit = {},
) {
    val controllerDescription = stringResource(R.string.ds_cd_calendar_controller)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .semantics { contentDescription = controllerDescription },
    ) {
        val today = LocalDate.now()
        val isOnToday = YearMonth.from(currentDate) == YearMonth.from(today) &&
            (selectedDate == null || selectedDate == today)

        IconButton(onClick = onGotoTodayClick) {
            if (!isOnToday) {
                Image(
                    painter = painterResource(R.drawable.ic_return),
                    contentDescription = stringResource(R.string.ds_cd_go_today),
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        Text(
            text = stringResource(R.string.ds_year_month, currentDate.year, currentDate.monthValue),
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textOnBackground,
        )

        if (showSyncButton) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onSyncClick),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_link),
                    contentDescription = stringResource(R.string.ds_cd_sync),
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
                    modifier = Modifier.size(28.dp),
                )
            }
        } else {
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}
