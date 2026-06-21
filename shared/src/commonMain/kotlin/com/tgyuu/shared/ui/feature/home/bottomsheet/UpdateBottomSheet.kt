package com.tgyuu.shared.ui.feature.home.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.schedule_update_info
import ebbingplanner.shared.generated.resources.schedule_update_repeat_cycle
import ebbingplanner.shared.generated.resources.schedule_update_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UpdateBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickUpdateDate: (TodoScheduleUiModel) -> Unit,
    onClickUpdateInfo: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.schedule_update_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.schedule_update_info),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickUpdateInfo(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(Res.string.schedule_update_repeat_cycle),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickUpdateDate(selectedSchedule) }
                    .height(62.dp),
            )
        }
    }
}
