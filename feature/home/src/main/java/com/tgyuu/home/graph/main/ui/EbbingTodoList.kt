package com.tgyuu.home.graph.main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

@Composable
internal fun EbbingTodoList(
    listState: LazyListState,
    sortType: SortType,
    selectedDate: LocalDate,
    todoLists: List<TodoSchedule>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onSelectDate: (LocalDate) -> Unit,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    onAddTodoClick: () -> Unit,
    onSortTypeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE },
    )

    var prevPage by remember { mutableIntStateOf(initialPage) }
    LaunchedEffect(pagerState.currentPage) {
        val newPage = pagerState.currentPage
        val delta = newPage - prevPage
        if (delta != 0) {
            onSelectDate(selectedDate.plusDays(delta.toLong()))
            prevPage = newPage
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        TodoHeader(
            displayDate = selectedDate,
            count = todoLists.size,
            sortType = sortType,
            onAddTodoClick = onAddTodoClick,
            onSortTypeClick = onSortTypeClick,
        )

        HorizontalPager(state = pagerState) { page ->
            TodoPage(
                date = selectedDate,
                todos = todoLists,
                schedulesByTodoInfo = schedulesByTodoInfo,
                listState = listState,
                onCheckedChange = onCheckedChange,
                onEdit = onEditScheduleClick
            )
        }
    }
}

@Composable
private fun TodoHeader(
    displayDate: LocalDate,
    count: Int,
    sortType: SortType,
    onAddTodoClick: () -> Unit,
    onSortTypeClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        val dateText = if (displayDate == LocalDate.now()) "오늘"
        else "${displayDate.monthValue}월 ${displayDate.dayOfMonth}일"
        Text(
            text = "$dateText  할 일 $count",
            style = EbbingTheme.typography.bodyMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSortTypeClick() }
                .padding(end = 16.dp)
        ) {
            Text(
                text = sortType.displayName,
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black
            )
            Image(
                painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
                modifier = Modifier.size(20.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = EbbingTheme.colors.background,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(EbbingTheme.colors.primaryDefault)
                .clickable { onAddTodoClick() }
                .padding(4.dp)
        )
    }
}

@Composable
private fun TodoPage(
    date: LocalDate,
    todos: List<TodoSchedule>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    listState: LazyListState,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEdit: (TodoSchedule) -> Unit
) {
    if (todos.isNotEmpty()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = todos,
                key = { it.id },
            ) { item ->
                TodoListCard(
                    todo = item,
                    todosWithSameInfo = schedulesByTodoInfo[item.infoId] ?: emptyList(),
                    onCheckedChange = onCheckedChange,
                    onEditScheduleClick = onEdit,
                    modifier = Modifier.animateItem()
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    } else {
        Text(
            text = "${date.monthValue}월 ${date.dayOfMonth}일 스케줄이 없어요.\n우측 상단 + 버튼을 눌러 새로운 스케줄을 만들어보세요.",
            style = EbbingTheme.typography.bodySM,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark3,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
        )
    }
}

@Composable
private fun TodoListCard(
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
                            maxLines = 1,
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
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            FlowRow(modifier = Modifier.weight(1f)) {
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

                    Image(
                        painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_3dots),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEditScheduleClick(todo) }
                    )
                }

                EbbingCheck(
                    checked = todo.isDone,
                    colorValue = todo.color,
                    onCheckedChange = { onCheckedChange(todo) },
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        EbbingVisibleAnimation(visible = todo.memo.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(end = 32.dp, top = 4.dp, bottom = 4.dp),
            ) {
                Image(
                    painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_memo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                    modifier = Modifier.size(16.dp),
                )

                Text(
                    text = todo.memo,
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
