package com.tgyuu.home.graph.main.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

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
        EbbingBottomSheetHeader(title = stringResource(R.string.home_delay_method))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.home_delay_single),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelaySingle(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(R.string.home_delay_all),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
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
