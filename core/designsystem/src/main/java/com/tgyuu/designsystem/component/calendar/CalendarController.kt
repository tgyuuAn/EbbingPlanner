package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tgyuu.common.now
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingTextToggle
import com.tgyuu.designsystem.foundation.EbbingTheme
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarController(
    currentDate: LocalDate,
    selectedDate: LocalDate?,
    onGotoTodayClick: () -> Unit,
    modifier: Modifier = Modifier,
    showSyncButton: Boolean = true,
    onSyncClick: () -> Unit = {},
    showViewToggle: Boolean = false,
    isWeekView: Boolean = false,
    onViewToggle: (Boolean) -> Unit = {},
) {
    val controllerDescription = stringResource(R.string.ds_cd_calendar_controller)
    val today = LocalDate.now()
    val isOnToday = currentDate.year == today.year &&
        currentDate.monthNumber == today.monthNumber &&
        (selectedDate == null || selectedDate == today)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .semantics { contentDescription = controllerDescription },
    ) {
        Text(
            text = stringResource(R.string.ds_year_month, currentDate.year, currentDate.monthNumber),
            style = EbbingTheme.typography.heading20B,
            color = EbbingTheme.colors.textOnBackground,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!isOnToday) {
                TodayButton(onClick = onGotoTodayClick)
            } else if (showSyncButton) {
                SyncButton(onClick = onSyncClick)
            }

            if (showViewToggle) {
                EbbingTextToggle(
                    firstLabel = stringResource(R.string.calendar_view_month),
                    secondLabel = stringResource(R.string.calendar_view_week),
                    selectedFirst = !isWeekView,
                    onSelectedChange = { toMonth -> onViewToggle(!toMonth) },
                )
            }
        }
    }
}

@Composable
private fun TodayButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(100.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(shape)
            .background(EbbingTheme.colors.fillNormal)
            .border(width = 1.dp, color = EbbingTheme.colors.strokeOutline, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_return),
            contentDescription = stringResource(R.string.ds_cd_go_today),
            colorFilter = ColorFilter.tint(EbbingTheme.colors.strokeIcon),
            modifier = Modifier
                .size(20.dp)
                .padding(3.dp),
        )
        Text(
            text = stringResource(R.string.calendar_today),
            style = EbbingTheme.typography.caption12R,
            color = EbbingTheme.colors.textSub,
        )
    }
}

@Composable
private fun SyncButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_link),
            contentDescription = stringResource(R.string.ds_cd_sync),
            colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
            modifier = Modifier.size(24.dp),
        )
    }
}
