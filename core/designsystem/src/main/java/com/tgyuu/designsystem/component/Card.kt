package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule

@Composable
fun TodoListCard(
    todo: TodoSchedule,
    todosWithSameInfo: List<TodoSchedule>,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min),
        ) {
            VerticalDivider(
                thickness = 8.dp,
                color = Color(todo.color),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EbbingTheme.colors.light3)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = todo.title,
                            style = EbbingTheme.typography.bodyMSB,
                            color = EbbingTheme.colors.black,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = todo.name,
                            style = EbbingTheme.typography.bodyMM,
                            color = EbbingTheme.colors.dark1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            FlowRow {
                                todosWithSameInfo.forEach {
                                    EbbingCheck(
                                        checked = it.isDone,
                                        colorValue = it.color,
                                        onCheckedChange = {},
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }

                            Text(
                                text = "우선도 : ${todo.priority}",
                                style = EbbingTheme.typography.bodySSB,
                                color = EbbingTheme.colors.dark1,
                                maxLines = 1,
                                textAlign = TextAlign.End,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 12.dp, bottom = 2.dp),
                            )
                        }
                    }
                }

                Image(
                    painter = painterResource(R.drawable.ic_3dots),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEditScheduleClick(todo) },
                )
            }

            EbbingCheck(
                checked = todo.isDone,
                colorValue = todo.color,
                onCheckedChange = { onCheckedChange(todo) },
                modifier = Modifier.size(24.dp),
            )
        }

        EbbingVisibleAnimation(todo.memo.isNotEmpty()) { TodoListMemoContent(todo.memo) }
    }
}

@Composable
private fun TodoListMemoContent(
    memo: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(end = 32.dp, top = 4.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_memo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
            modifier = Modifier.size(16.dp),
        )

        Text(
            text = memo,
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.weight(1f),
        )
    }
}
