package com.tgyuu.shared.ui.feature.schedule.bottomsheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.tag.bottomsheet.TAG_COLORS
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.schedule_delay_all
import ebbingplanner.shared.generated.resources.schedule_delay_single
import ebbingplanner.shared.generated.resources.schedule_delay_title
import ebbingplanner.shared.generated.resources.schedule_delete_remaining
import ebbingplanner.shared.generated.resources.schedule_delete_single
import ebbingplanner.shared.generated.resources.schedule_delete_title
import ebbingplanner.shared.generated.resources.schedule_option_add_memo
import ebbingplanner.shared.generated.resources.schedule_option_delay_tomorrow
import ebbingplanner.shared.generated.resources.schedule_option_delete
import ebbingplanner.shared.generated.resources.schedule_option_delete_memo
import ebbingplanner.shared.generated.resources.schedule_option_edit
import ebbingplanner.shared.generated.resources.schedule_option_edit_memo
import ebbingplanner.shared.generated.resources.schedule_options_subtitle
import ebbingplanner.shared.generated.resources.schedule_options_title
import ebbingplanner.shared.generated.resources.schedule_tag_color_label
import ebbingplanner.shared.generated.resources.schedule_tag_delete_label
import ebbingplanner.shared.generated.resources.schedule_tag_edit_title
import ebbingplanner.shared.generated.resources.schedule_tag_name_hint
import ebbingplanner.shared.generated.resources.schedule_tag_name_label
import ebbingplanner.shared.generated.resources.schedule_tag_save_button
import ebbingplanner.shared.generated.resources.schedule_update_info
import ebbingplanner.shared.generated.resources.schedule_update_repeat_cycle
import ebbingplanner.shared.generated.resources.schedule_update_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun TagEditBottomSheet(
    originName: String,
    originColor: Int,
    onSave: (name: String, color: Int) -> Unit,
    onDelete: () -> Unit,
) {
    var name by remember(originName) { mutableStateOf(originName) }
    var selectedColor by remember(originColor) { mutableIntStateOf(originColor) }
    var isColorExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .animateContentSize(),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.schedule_tag_edit_title, originName))

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(Res.string.schedule_tag_name_label),
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.black,
        )

        Spacer(modifier = Modifier.height(8.dp))

        com.tgyuu.shared.designsystem.component.EbbingTextInputDefault(
            value = name,
            onValueChange = { name = it },
            hint = stringResource(Res.string.schedule_tag_name_hint),
            limit = 20,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isColorExpanded = !isColorExpanded },
        ) {
            Text(
                text = stringResource(Res.string.schedule_tag_color_label),
                style = EbbingTheme.typography.bodySSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )

            Spacer(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(selectedColor))
            )
        }

        if (isColorExpanded) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .heightIn(max = 228.dp),
            ) {
                items(TAG_COLORS) { colorValue ->
                    val baseColor = Color(colorValue)
                    val displayColor = if (selectedColor == colorValue) lerp(baseColor, Color.Black, 0.2f) else baseColor

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(displayColor)
                                .clickable {
                                    selectedColor = colorValue
                                    isColorExpanded = false
                                }
                        )

                        if (selectedColor == colorValue) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onDelete() },
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = EbbingTheme.colors.error,
                modifier = Modifier.size(20.dp),
            )

            Text(
                text = stringResource(Res.string.schedule_tag_delete_label, originName),
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.error,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EbbingSolidButton(
            label = stringResource(Res.string.schedule_tag_save_button),
            onClick = { onSave(name, selectedColor) },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        )
    }
}

@Composable
fun ScheduleOptionsBottomSheet(
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
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_option_edit), onClick = { onClickUpdate(selectedSchedule) })
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_option_delete), onClick = { onClickDelete(selectedSchedule) })
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_option_delay_tomorrow), onClick = { onClickDelay(selectedSchedule) })
            BottomSheetOptionItem(
                text = if (selectedSchedule.memo.isEmpty()) stringResource(Res.string.schedule_option_add_memo) else stringResource(Res.string.schedule_option_edit_memo),
                onClick = { onClickMemo(selectedSchedule) },
            )
            if (selectedSchedule.memo.isNotEmpty()) {
                BottomSheetOptionItem(text = stringResource(Res.string.schedule_option_delete_memo), onClick = { onClickDeleteMemo(selectedSchedule) })
            }
        }
    }
}

@Composable
fun ScheduleUpdateBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickUpdateInfo: (TodoScheduleUiModel) -> Unit,
    onClickUpdateDate: (TodoScheduleUiModel) -> Unit,
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
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_update_info), onClick = { onClickUpdateInfo(selectedSchedule) })
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_update_repeat_cycle), onClick = { onClickUpdateDate(selectedSchedule) })
        }
    }
}

@Composable
fun ScheduleDeleteBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickDeleteSingle: (TodoScheduleUiModel) -> Unit,
    onClickDeleteRemaining: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(Res.string.schedule_delete_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_delete_single), onClick = { onClickDeleteSingle(selectedSchedule) })
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_delete_remaining), onClick = { onClickDeleteRemaining(selectedSchedule) })
        }
    }
}

@Composable
fun ScheduleDelayBottomSheet(
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
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_delay_single), onClick = { onClickDelaySingle(selectedSchedule) })
            BottomSheetOptionItem(text = stringResource(Res.string.schedule_delay_all), onClick = { onClickDelayAll(selectedSchedule) })
        }
    }
}

@Composable
private fun BottomSheetOptionItem(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(62.dp),
    ) {
        Text(
            text = text,
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
