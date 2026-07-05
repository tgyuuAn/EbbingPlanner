package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel

@Composable
fun TodoListCard(
    todo: TodoScheduleUiModel,
    todosWithSameInfo: List<TodoScheduleUiModel>,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleColor =
        if (todo.isDone) EbbingTheme.colors.textDisabled else EbbingTheme.colors.textOnBackground
    val subColor =
        if (todo.isDone) EbbingTheme.colors.textDisabled else EbbingTheme.colors.textSub
    val orderedSchedules = remember(todosWithSameInfo) { todosWithSameInfo.sortedBy { it.date } }
    val roundNumber = remember(orderedSchedules, todo.id) {
        orderedSchedules.indexOfFirst { it.id == todo.id }.coerceAtLeast(0) + 1
    }

    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        // 좌측 색상 인덱스 바
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(Color(todo.color)),
        )

        // 흰 배경 프레임 (우측 라운드 + 테두리)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                .background(EbbingTheme.colors.background)
                .border(
                    width = 1.dp,
                    color = EbbingTheme.colors.strokeOutline,
                    shape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp),
                )
                .padding(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                EbbingCheck(
                    checked = todo.isDone,
                    colorValue = todo.color,
                    onCheckedChange = { onCheckedChange(todo) },
                    modifier = Modifier.size(20.dp),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = todo.name,
                            style = EbbingTheme.typography.caption12R,
                            color = subColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            // 제목이 길어도 3dots 영역과 겹치지 않도록 우측 여백 확보
                            modifier = Modifier.padding(end = 24.dp),
                        ) {
                            EbbingClickableText(
                                clickableText = todo.title,
                                style = EbbingTheme.typography.heading16B,
                                color = titleColor,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )

                            if (todo.isPinned) {
                                Image(
                                    painter = painterResource(R.drawable.ic_pin),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(subColor),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.card_round_count, roundNumber),
                            style = EbbingTheme.typography.caption12R,
                            color = EbbingTheme.colors.textDisabled,
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(1.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            orderedSchedules.forEach { schedule ->
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (schedule.isDone) Color(todo.color) else Color.Transparent
                                        )
                                        .border(1.25.dp, Color(todo.color), CircleShape),
                                )
                            }
                        }
                    }

                    EbbingVisibleAnimation(todo.memo.originalText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(EbbingTheme.colors.fillTextfield)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            EbbingClickableText(
                                clickableText = todo.memo,
                                style = EbbingTheme.typography.caption12R,
                                color = subColor,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            // 우상단 3-dots (수정 옵션)
            Image(
                painter = painterResource(R.drawable.ic_3dots),
                contentDescription = null,
                colorFilter = ColorFilter.tint(subColor),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clickable { onEditScheduleClick(todo) },
            )
        }
    }
}
