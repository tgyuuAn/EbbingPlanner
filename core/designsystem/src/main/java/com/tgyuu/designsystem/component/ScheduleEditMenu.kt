package com.tgyuu.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.model.TodoScheduleUiModel

/**
 * 태블릿 일정 편집(미트볼) 드롭다운.
 * 화면이 넘겨주면 미트볼이 바텀시트 대신 바로 아래에 [EbbingOptionsDropdown]을 띄워요.
 */
class ScheduleEditMenu(
    val title: String,
    /** 메뉴가 열릴 때(분석 로깅 등) */
    val onOpen: (TodoScheduleUiModel) -> Unit = {},
    val items: @Composable ColumnScope.(schedule: TodoScheduleUiModel, dismiss: () -> Unit) -> Unit,
)

@Composable
fun ScheduleEditMenuAnchor(
    schedule: TodoScheduleUiModel,
    editMenu: ScheduleEditMenu?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trigger: @Composable () -> Unit,
) {
    var expanded by rememberSaveable(schedule.id) { mutableStateOf(false) }

    Box(
        modifier = modifier.clickable {
            if (editMenu == null) {
                onClick()
            } else {
                editMenu.onOpen(schedule)
                expanded = true
            }
        },
    ) {
        trigger()

        if (editMenu != null && expanded) {
            EbbingOptionsDropdown(
                title = editMenu.title,
                onDismissRequest = { expanded = false },
            ) {
                editMenu.items(this, schedule) { expanded = false }
            }
        }
    }
}
