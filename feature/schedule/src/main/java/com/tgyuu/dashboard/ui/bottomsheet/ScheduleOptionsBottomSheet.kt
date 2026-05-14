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
        EbbingBottomSheetHeader(title = "'$originName' 태그 편집")

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "태그 이름",
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textOnBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        EbbingTextInputDefault(
            value = name,
            onValueChange = { name = it },
            hint = "태그 이름을 입력하세요",
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
                text = "색상",
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
                text = "'$originName' 태그 삭제",
                style = EbbingTheme.typography.body14M,
                color = EbbingTheme.colors.statusError,
                modifier = Modifier
                    .clickable { onDelete() }
                    .padding(vertical = 4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EbbingSolidButton(
            label = "저장하기",
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
            title = "편집",
            subTitle = "${selectedSchedule.title.originalText} 일정을 어떻게 할까요?"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 22.dp),
        ) {
            Text(
                text = "수정하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickUpdate(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = "삭제하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelete(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = "내일로 미루기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDelay(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = if (selectedSchedule.memo.originalText.isEmpty()) "메모 추가하기" else "메모 수정하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickMemo(selectedSchedule) }
                    .height(62.dp),
            )

            if (selectedSchedule.memo.originalText.isNotEmpty()) {
                Text(
                    text = "메모 지우기",
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textOnBackground,
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
        EbbingBottomSheetHeader(title = "수정 방법")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = "일정 정보 수정하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickUpdateInfo(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = "연관된 일정 반복 주기 재설정하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
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
        EbbingBottomSheetHeader(title = "삭제 방법")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = "해당 일정만 삭제하기",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDeleteSingle(selectedSchedule) }
                    .height(62.dp),
            )

            Text(
                text = "연계된 이후 일정 전부 삭제",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.textOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickDeleteRemaining(selectedSchedule) }
                    .height(62.dp),
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
        EbbingBottomSheetHeader(title = "미루기 방법")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        ) {
            Text(
                text = "이 일정만 미루기",
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
                text = "이후 일정 모두 미루기",
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
