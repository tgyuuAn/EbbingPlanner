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
import ebbingplanner.shared.generated.resources.schedule_option_add_memo
import ebbingplanner.shared.generated.resources.schedule_option_delay_tomorrow
import ebbingplanner.shared.generated.resources.schedule_option_delete
import ebbingplanner.shared.generated.resources.schedule_option_delete_memo
import ebbingplanner.shared.generated.resources.schedule_option_edit
import ebbingplanner.shared.generated.resources.schedule_option_edit_memo
import ebbingplanner.shared.generated.resources.schedule_options_subtitle
import ebbingplanner.shared.generated.resources.schedule_options_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OptionsBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickUpdate: (TodoScheduleUiModel) -> Unit,
    onClickDelete: (TodoScheduleUiModel) -> Unit,
    onClickDelay: (TodoScheduleUiModel) -> Unit,
    onClickMemo: (TodoScheduleUiModel) -> Unit,
    onClickDeleteMemo: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(Res.string.schedule_options_title),
            subTitle = stringResource(Res.string.schedule_options_subtitle, selectedSchedule.title)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 22.dp),
        ) {
            Text(
                text = stringResource(Res.string.schedule_option_edit),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickUpdate(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(Res.string.schedule_option_delete),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelete(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(Res.string.schedule_option_delay_tomorrow),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelay(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = if (selectedSchedule.memo.isEmpty()) stringResource(Res.string.schedule_option_add_memo) else stringResource(Res.string.schedule_option_edit_memo),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickMemo(selectedSchedule) }
                    .height(62.dp),
            )

            if (selectedSchedule.memo.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.schedule_option_delete_memo),
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickDeleteMemo(selectedSchedule) }
                        .height(62.dp),
                )
            }
        }
    }
}
