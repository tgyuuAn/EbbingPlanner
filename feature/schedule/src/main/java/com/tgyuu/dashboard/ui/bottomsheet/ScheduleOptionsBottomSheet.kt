package com.tgyuu.dashboard.ui.bottomsheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.common.util.verticalScrollbar
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.ColorOptions
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

@Composable
internal fun TagEditBottomSheet(
    originName: String,
    originColor: Int,
    onSave: (name: String, color: Int) -> Unit,
    onDelete: () -> Unit,
) {
    var name by remember(originName) { mutableStateOf(originName) }
    var selectedColor by remember(originColor) { mutableIntStateOf(originColor) }
    var isColorExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .animateContentSize(),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(R.string.schedule_tag_edit_title, originName)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.schedule_tag_name_label),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        EbbingTextInputDefault(
            value = name,
            onValueChange = { name = it },
            hint = stringResource(R.string.schedule_tag_name_hint),
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
                text = stringResource(R.string.schedule_tag_color_label),
                style = EbbingTheme.typography.heading14SB,
                color = EbbingTheme.colors.textOnBackground,
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
                state = listState,
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .heightIn(max = 228.dp)
                    .verticalScrollbar(
                        state = listState,
                        color = EbbingTheme.colors.fillDisabled,
                    ),
            ) {
                items(ColorOptions) { colorValue ->
                    val baseColor = Color(colorValue)
                    val displayColor = ebbingAnimateColorAsState(
                        targetValue = if (selectedColor == colorValue) lerp(
                            baseColor,
                            Color.Black,
                            0.2f
                        )
                        else baseColor
                    )

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

                        EbbingVisibleAnimation(selectedColor == colorValue) {
                            Image(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_trashcan),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.statusError),
                modifier = Modifier.size(20.dp),
            )

            Text(
                text = stringResource(R.string.schedule_tag_delete_label, originName),
                style = EbbingTheme.typography.body14M,
                color = EbbingTheme.colors.statusError,
                modifier = Modifier
                    .clickable { onDelete() }
                    .padding(vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EbbingSolidButton(
            label = stringResource(R.string.schedule_tag_save_button),
            onClick = { onSave(name, selectedColor) },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        )
    }
}

@Composable
internal fun ScheduleOptionsBottomSheet(
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
            title = stringResource(R.string.schedule_options_title),
            subTitle = stringResource(
                R.string.schedule_options_subtitle,
                selectedSchedule.title.originalText,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 22.dp),
        ) {
            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_option_edit),
                onClick = { onClickUpdate(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_option_delete),
                onClick = { onClickDelete(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_option_delay_tomorrow),
                onClick = { onClickDelay(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = if (selectedSchedule.memo.originalText.isEmpty()) {
                    stringResource(R.string.schedule_option_add_memo)
                } else {
                    stringResource(R.string.schedule_option_edit_memo)
                },
                onClick = { onClickMemo(selectedSchedule) },
            )

            if (selectedSchedule.memo.originalText.isNotEmpty()) {
                BottomSheetOptionItem(
                    text = stringResource(R.string.schedule_option_delete_memo),
                    onClick = { onClickDeleteMemo(selectedSchedule) },
                )
            }
        }
    }
}

@Composable
internal fun ScheduleUpdateBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickUpdateInfo: (TodoScheduleUiModel) -> Unit,
    onClickUpdateDate: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(R.string.schedule_update_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_update_info),
                onClick = { onClickUpdateInfo(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_update_repeat_cycle),
                onClick = { onClickUpdateDate(selectedSchedule) },
            )
        }
    }
}

@Composable
internal fun ScheduleDeleteBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickDeleteSingle: (TodoScheduleUiModel) -> Unit,
    onClickDeleteRemaining: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(R.string.schedule_delete_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_delete_single),
                onClick = { onClickDeleteSingle(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_delete_remaining),
                onClick = { onClickDeleteRemaining(selectedSchedule) },
            )
        }
    }
}

@Composable
internal fun ScheduleDelayBottomSheet(
    selectedSchedule: TodoScheduleUiModel,
    onClickDelaySingle: (TodoScheduleUiModel) -> Unit,
    onClickDelayAll: (TodoScheduleUiModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = stringResource(R.string.schedule_delay_title))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_delay_single),
                onClick = { onClickDelaySingle(selectedSchedule) },
            )

            BottomSheetOptionItem(
                text = stringResource(R.string.schedule_delay_all),
                onClick = { onClickDelayAll(selectedSchedule) },
            )
        }
    }
}

@Composable
private fun BottomSheetOptionItem(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .height(62.dp),
    ) {
        Text(
            text = text,
            style = EbbingTheme.typography.body16M,
            color = EbbingTheme.colors.textOnBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
