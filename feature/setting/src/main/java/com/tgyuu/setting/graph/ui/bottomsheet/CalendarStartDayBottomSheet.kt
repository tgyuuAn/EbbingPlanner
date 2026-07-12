package com.tgyuu.setting.graph.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault

@Composable
internal fun CalendarStartDayBottomSheet(
    originMondayStart: Boolean,
    onUpdateClick: (Boolean) -> Unit,
) {
    var newMondayStart by remember(originMondayStart) { mutableStateOf(originMondayStart) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(R.string.setting_calendar_start_day))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            EbbingBottomSheetListItemDefault(
                label = stringResource(R.string.setting_monday),
                checked = newMondayStart,
                onChecked = { newMondayStart = true },
            )

            EbbingBottomSheetListItemDefault(
                label = stringResource(R.string.setting_sunday),
                checked = !newMondayStart,
                onChecked = { newMondayStart = false },
            )

            EbbingSolidButton(
                label = stringResource(R.string.setting_apply_action),
                onClick = { onUpdateClick(newMondayStart) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 10.dp),
            )
        }
    }
}
