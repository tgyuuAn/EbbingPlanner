package com.tgyuu.home.graph.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import com.tgyuu.home.graph.main.contract.HomeIntent.OnCheckedChange
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    HomeScreen(
        isLoading = state.isLoading,
        schedulesByDateMap = state.schedulesByDateMap,
        schedulesByTodoInfo = state.schedulesByTodoInfo,
        onAddTodoClick = { viewModel.onIntent(OnAddTodoClick(it)) },
        onCheckedChange = { viewModel.onIntent(OnCheckedChange(it)) }
    )
}

@Composable
private fun HomeScreen(
    isLoading: Boolean,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val calendarState = rememberCalendarState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingCalendar(
            calendarState = calendarState,
            schedulesByDateMap = schedulesByDateMap,
            onDateSelect = { selectedDate = it },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider(
            thickness = 12.dp,
            color = EbbingTheme.colors.light3,
            modifier = Modifier.fillMaxWidth()
        )

        if (isLoading) {
            Spacer(modifier = Modifier.weight(1f))

            CircularProgressIndicator(
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.size(50.dp),
                strokeWidth = 6.dp,
            )

            Spacer(modifier = Modifier.weight(1f))
        } else {
            EbbingTodoList(
                todoLists = schedulesByDateMap[selectedDate] ?: emptyList(),
                schedulesByTodoInfo = schedulesByTodoInfo,
                onAddTodoClick = { onAddTodoClick(selectedDate) },
                onCheckedChange = onCheckedChange,
                onEditScheduleClick = {},
            )
        }
    }
}

@Composable
private fun EbbingTodoList(
    todoLists: List<TodoSchedule>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    onAddTodoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Crossfade(
        targetState = todoLists,
        animationSpec = tween(durationMillis = 300)
    ) { todoLists ->
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 24.dp),
            ) {
                Text(
                    text = "오늘 할 일 ${todoLists.size}",
                    style = EbbingTheme.typography.headingMSB,
                    color = EbbingTheme.colors.black,
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = EbbingTheme.colors.white,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(EbbingTheme.colors.primaryDefault)
                        .clickable { onAddTodoClick() },
                )
            }

            LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = todoLists,
                    key = { idx, item -> item.id },
                ) { idx, item ->
                    TodoListCard(
                        todo = item,
                        todosWithSameInfo = schedulesByTodoInfo[item.infoId] ?: emptyList(),
                        onCheckedChange = onCheckedChange,
                        onEditScheduleClick = onEditScheduleClick,
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 6.dp,
                        )
                    )
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        EbbingCheck(
            checked = todo.isDone,
            colorValue = todo.color,
            onCheckedChange = { onCheckedChange(todo) },
            modifier = Modifier.size(24.dp),
        )

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

                if (todo.memo.isNotEmpty()) {
                    Text(
                        text = todo.memo,
                        style = EbbingTheme.typography.bodyMM,
                        color = EbbingTheme.colors.error,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    todosWithSameInfo.forEach {
                        EbbingCheck(
                            checked = it.isDone,
                            colorValue = it.color,
                            onCheckedChange = {},
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Image(
                painter = painterResource(R.drawable.ic_3dots),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onEditScheduleClick(todo) }
            )
        }
    }
}

@Preview
@Composable
private fun Preview1() {
    BasePreview {
        HomeScreen(
            isLoading = true,
            schedulesByDateMap = emptyMap(),
            schedulesByTodoInfo = emptyMap(),
            onAddTodoClick = {},
            onCheckedChange = {},
        )
    }
}
