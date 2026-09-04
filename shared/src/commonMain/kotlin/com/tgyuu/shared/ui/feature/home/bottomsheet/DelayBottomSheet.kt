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
import ebbingplanner.shared.generated.resources.schedule_delay_all
import ebbingplanner.shared.generated.resources.schedule_delay_single
import ebbingplanner.shared.generated.resources.schedule_delay_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DelayBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickDelaySingle: (TodoScheduleUiModel) -> Unit,
    onClickDelayAll: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.schedule_delay_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = stringResource(Res.string.schedule_delay_single),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelaySingle(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(Res.string.schedule_delay_all),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelayAll(selectedSchedule) }
                    .height(62.dp),
            )
        }
    }
}
