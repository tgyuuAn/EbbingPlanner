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
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tgyuu.home.graph.main.bottomsheet.EditScheduleBottomSheet
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import com.tgyuu.home.graph.main.contract.HomeIntent.OnCheckedChange
import com.tgyuu.home.graph.main.dialog.DelayDialog
import com.tgyuu.home.graph.main.dialog.DeleteDialog
import com.tgyuu.home.graph.main.dialog.DialogType
import com.tgyuu.home.graph.main.dialog.DialogType.Delete
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isShowDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    if (isShowDialog && dialogType != null) {
        when (val dt = dialogType) {
            is Delete -> DeleteDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDeleteClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDeleteScheduleClick(dt.schedule))
                },
            )

            is DialogType.Delay -> DelayDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDelayClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDelayScheduleClick(dt.schedule))
                },
            )

            else -> Unit
        }
    }

    HomeScreen(
        isLoading = state.isLoading,
        schedulesByDateMap = state.schedulesByDateMap,
        schedulesByTodoInfo = state.schedulesByTodoInfo,
        onAddTodoClick = { viewModel.onIntent(OnAddTodoClick(it)) },
        onCheckedChange = { viewModel.onIntent(OnCheckedChange(it)) },
        onEditScheduleClick = {
            viewModel.onIntent(HomeIntent.OnEditScheduleClick({
                EditScheduleBottomSheet(
                    selectedSchedule = it,
                    onUpdateClick = { viewModel.onIntent(HomeIntent.OnUpdateScheduleClick(it)) },
                    onDelayClick = {
                        dialogType = DialogType.Delay(it)
                        isShowDialog = true
                    },
                    onDeleteClick = {
                        dialogType = DialogType.Delete(it)
                        isShowDialog = true
                    },
                )
            }))
        },
    )
}

@Composable
private fun HomeScreen(
    isLoading: Boolean,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
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
            onDateSelect = {
                if (selectedDate != it) {
                    scope.launch {
                        selectedDate = it
                        listState.animateScrollToItem(0)
                    }
                }
            },
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
                listState = listState,
                selectedDate = selectedDate,
                todoLists = schedulesByDateMap[selectedDate] ?: emptyList(),
                schedulesByTodoInfo = schedulesByTodoInfo,
                onAddTodoClick = { onAddTodoClick(selectedDate) },
                onCheckedChange = onCheckedChange,
                onEditScheduleClick = onEditScheduleClick,
            )
        }
    }
}

@Composable
private fun EbbingTodoList(
    listState: LazyListState,
    selectedDate: LocalDate,
    todoLists: List<TodoSchedule>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    onAddTodoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                val displayDate = if (selectedDate == LocalDate.now()) "오늘"
                else "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일"

                Text(
                    text = "${displayDate}  할 일 ${todoLists.size}",
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
            onEditScheduleClick = {},
        )
    }
}
