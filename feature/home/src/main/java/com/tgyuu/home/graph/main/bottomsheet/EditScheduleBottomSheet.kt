package com.tgyuu.home.graph.main.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule

@Composable
internal fun EditScheduleBottomSheet(
    selectedSchedule: TodoSchedule,
    onUpdateClick: (TodoSchedule) -> Unit,
    onDeleteClick: (TodoSchedule) -> Unit,
    onDelayClick: (TodoSchedule) -> Unit,
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
                    .clickable { onUpdateClick(selectedSchedule) }
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
                    .clickable { onDeleteClick(selectedSchedule) }
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
                    .clickable { onDelayClick(selectedSchedule) }
                    .height(62.dp),
            )
        }
    }
}
