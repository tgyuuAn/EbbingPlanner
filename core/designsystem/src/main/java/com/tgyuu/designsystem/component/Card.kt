package com.tgyuu.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val outlineColor = EbbingTheme.colors.strokeOutline
    val orderedSchedules = remember(todosWithSameInfo) { todosWithSameInfo.sortedBy { it.date } }
    val roundNumber = remember(orderedSchedules, todo.id) {
        orderedSchedules.indexOfFirst { it.id == todo.id }.coerceAtLeast(0) + 1
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                .background(EbbingTheme.colors.background)
                .drawBehind {
                    val strokePx = 1.dp.toPx()
                    val radiusPx = 6.dp.toPx()
                    val inset = strokePx / 2
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, inset)
                        lineTo(w - radiusPx, inset)
                        arcTo(Rect(w - radiusPx * 2 + inset, inset, w - inset, radiusPx * 2 - inset), 270f, 90f, false)
                        lineTo(w - inset, h - radiusPx)
                        arcTo(Rect(w - radiusPx * 2 + inset, h - radiusPx * 2 + inset, w - inset, h - inset), 0f, 90f, false)
                        lineTo(0f, h - inset)
                    }
                    drawPath(path, color = outlineColor, style = Stroke(width = strokePx))
                }
                .padding(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EbbingTheme.colors.fillTextfield)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = todo.name,
                            style = EbbingTheme.typography.body14M,
                            color = subColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 24.dp),
                        ) {
                            FlowRow(modifier = Modifier.weight(1f)) {
                                todosWithSameInfo.forEach {
                                    EbbingCheck(
                                        checked = it.isDone,
                                        colorValue = it.color,
                                        onCheckedChange = {},
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }

                            Text(
                                text = "우선도 : ${todo.priority}",
                                style = EbbingTheme.typography.heading14SB,
                                color = EbbingTheme.colors.textSub,
                                maxLines = 1,
                                textAlign = TextAlign.End,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )

                            if (todo.isPinned) {
                                Image(
                                    painter = painterResource(R.drawable.ic_pin),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(EbbingTheme.colors.strokeIcon),
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
                            style = EbbingTheme.typography.body14M,
                            color = EbbingTheme.colors.textDisabled,
                        )

                EbbingCheck(
                    checked = todo.isDone,
                    colorValue = todo.color,
                    onCheckedChange = { onCheckedChange(todo) },
                    modifier = Modifier.size(20.dp),
                )
            }

            // 우상단 3-dots (수정 옵션)
            Image(
                painter = painterResource(R.drawable.ic_3dots),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.strokeIcon),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clickable { onEditScheduleClick(todo) },
            )
        }

        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                    .background(Color(todo.color)),
            )
        }
    }
}
