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
import com.tgyuu.designsystem.component.EbbingOptionsDropdownItem
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

/** 일정 편집 액션. 폰 바텀시트와 태블릿 드롭다운이 같은 동작을 공유해요 */
internal class ScheduleOptionActions(
    val onClickUpdate: (TodoScheduleUiModel) -> Unit,
    val onClickDelete: (TodoScheduleUiModel) -> Unit,
    val onClickDelay: (TodoScheduleUiModel) -> Unit,
    val onClickMemo: (TodoScheduleUiModel) -> Unit,
    val onClickDeleteMemo: (TodoScheduleUiModel) -> Unit,
)

@Composable
internal fun OptionsBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    actions: ScheduleOptionActions,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(R.string.home_options_title),
            subTitle = stringResource(
                R.string.home_options_subtitle,
                selectedSchedule.title.originalText,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 22.dp),
        ) {
            Text(
                text = stringResource(R.string.home_options_edit),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { actions.onClickUpdate(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(R.string.home_options_delete),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { actions.onClickDelete(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = stringResource(R.string.home_options_delay_tomorrow),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { actions.onClickDelay(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = if (selectedSchedule.memo.originalText.isEmpty()) stringResource(R.string.home_options_add_memo) else stringResource(R.string.home_options_edit_memo),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { actions.onClickMemo(selectedSchedule) }
                    .height(62.dp),
            )

            if (selectedSchedule.memo.originalText.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.home_options_delete_memo),
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textOnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { actions.onClickDeleteMemo(selectedSchedule) }
                        .height(62.dp),
                )
            }
        }
    }
}

/** 태블릿 미트볼 드롭다운 항목. 고르면 드롭다운을 닫고, 한 단계 더 들어가는 액션은 가운데 팝업으로 이어져요 */
@Composable
internal fun OptionsDropdownItems(
    selectedSchedule: TodoScheduleUiModel,
    actions: ScheduleOptionActions,
    onDismiss: () -> Unit,
) {
    val hasMemo = selectedSchedule.memo.originalText.isNotEmpty()
    val items = buildList {
        add(stringResource(R.string.home_options_edit) to actions.onClickUpdate)
        add(stringResource(R.string.home_options_delete) to actions.onClickDelete)
        add(stringResource(R.string.home_options_delay_tomorrow) to actions.onClickDelay)
        add(
            stringResource(if (hasMemo) R.string.home_options_edit_memo else R.string.home_options_add_memo)
                to actions.onClickMemo
        )
        if (hasMemo) add(stringResource(R.string.home_options_delete_memo) to actions.onClickDeleteMemo)
    }

    items.forEach { (label, action) ->
        EbbingOptionsDropdownItem(
            label = label,
            onClick = {
                onDismiss()
                action(selectedSchedule)
            },
        )
    }
}
