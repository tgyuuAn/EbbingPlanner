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
            title = "편집",
            subTitle = "${selectedSchedule.title} 일정을 어떻게 할까요?"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 22.dp),
        ) {
            Text(
                text = "수정하기",
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
                text = "삭제하기",
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
                text = "내일로 미루기",
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
                text = if (selectedSchedule.memo.isEmpty()) "메모 추가하기" else "메모 수정하기",
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
                    text = "메모 지우기",
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
