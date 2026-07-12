package com.tgyuu.shared.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.card_round_count
import ebbingplanner.shared.generated.resources.ic_3dots
import ebbingplanner.shared.generated.resources.ic_pin
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TodoListCard(
    todo: TodoScheduleUiModel,
    todosWithSameInfo: List<TodoScheduleUiModel>,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleColor =
        if (todo.isDone) EbbingTheme.colors.dark3 else EbbingTheme.colors.black
    val subColor =
        if (todo.isDone) EbbingTheme.colors.dark3 else EbbingTheme.colors.dark1
    val outlineColor = EbbingTheme.colors.light2
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
                            style = EbbingTheme.typography.bodySM,
                            color = subColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 24.dp),
                        ) {
                            Text(
                                text = todo.title,
                                style = EbbingTheme.typography.headingMB,
                                color = titleColor,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                            )

                            if (todo.isPinned) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_pin),
                                    contentDescription = null,
                                    tint = EbbingTheme.colors.dark2,
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
                            text = stringResource(Res.string.card_round_count, roundNumber),
                            style = EbbingTheme.typography.bodySM,
                            color = EbbingTheme.colors.dark3,
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

                    AnimatedVisibility(todo.memo.isNotEmpty()) {
                        var memoExpanded by remember(todo.id) { mutableStateOf(false) }
                        var memoOverflow by remember(todo.id) { mutableStateOf(false) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(EbbingTheme.colors.light3)
                                .then(
                                    if (memoOverflow) Modifier.clickable { memoExpanded = !memoExpanded }
                                    else Modifier
                                )
                                .animateContentSize()
                                .heightIn(min = 28.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = todo.memo,
                                style = EbbingTheme.typography.bodySM,
                                color = subColor,
                                maxLines = if (memoExpanded) Int.MAX_VALUE else 1,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { if (!memoExpanded) memoOverflow = it.hasVisualOverflow },
                                modifier = Modifier.weight(1f),
                            )

                            if (memoOverflow || memoExpanded) {
                                Icon(
                                    imageVector = if (memoExpanded) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = EbbingTheme.colors.dark2,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }

            // 우상단 3-dots (수정 옵션)
            Icon(
                painter = painterResource(Res.drawable.ic_3dots),
                contentDescription = null,
                tint = EbbingTheme.colors.dark2,
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
