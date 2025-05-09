package com.tgyuu.home.graph.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.ui.clickable
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import com.tgyuu.home.graph.main.contract.HomeIntent.OnCheckedChange
import com.tgyuu.home.graph.main.contract.HomeIntent.OnSortTypeClick
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.home.graph.main.ui.bottomsheet.EditScheduleBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.SortTypeBottomSheet
import com.tgyuu.home.graph.main.ui.dialog.DelayDialog
import com.tgyuu.home.graph.main.ui.dialog.DeleteDialog
import com.tgyuu.home.graph.main.ui.dialog.DialogType
import com.tgyuu.home.graph.main.ui.dialog.DialogType.Delete
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    workedDate: LocalDate,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
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
        workedDate = workedDate,
        state = state,
        onAddTodoClick = { viewModel.onIntent(OnAddTodoClick(it)) },
        onCheckedChange = { viewModel.onIntent(OnCheckedChange(it)) },
        onSortTypeClick = {
            viewModel.onIntent(OnSortTypeClick({
                SortTypeBottomSheet(
                    originSortType = state.sortType,
                    onUpdateClick = { viewModel.onIntent(HomeIntent.OnUpdateSortType(it)) },
                )
            }))
        },
        onEditScheduleClick = { schedule ->
            viewModel.onIntent(
                HomeIntent.OnEditScheduleClick {
                    EditScheduleBottomSheet(
                        selectedSchedule = schedule,
                        onDelayClick = { delayedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                dialogType = DialogType.Delay(delayedSchedule)
                                isShowDialog = true
                            }
                        },
                        onDeleteClick = { deletedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                dialogType = DialogType.Delete(deletedSchedule)
                                isShowDialog = true
                            }
                        },
                        onUpdateClick = { updatedSchedule ->
                            viewModel.onIntent(HomeIntent.OnUpdateScheduleClick(updatedSchedule))
                        }
                    )
                }
            )
        }
    )
}

@Composable
private fun HomeScreen(
    workedDate: LocalDate,
    state: HomeState,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoSchedule) -> Unit,
    onSortTypeClick: () -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var selectedDate by remember(workedDate) { mutableStateOf(workedDate) }
    val calendarState = rememberCalendarState()
    var isExpanded by remember { mutableStateOf(false) }
    var calendarHeight by remember { mutableStateOf(0.dp) }
    val animatedTopPadding by animateDpAsState(targetValue = if (isExpanded) 0.dp else calendarHeight)
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(workedDate)
    }

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val height = with(localDensity) {
                            coordinates.size.height.toDp()
                        }
                        calendarHeight = height
                    }
            ) {
                EbbingCalendar(
                    calendarState = calendarState,
                    schedulesByDateMap = state.schedulesByDateMap,
                    onDateSelect = {
                        if (selectedDate != it) {
                            scope.launch {
                                selectedDate = it
                                listState.animateScrollToItem(0)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(
                    thickness = 8.dp,
                    color = EbbingTheme.colors.light3,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = animatedTopPadding)
                    .background(EbbingTheme.colors.background)
            ) {
                Image(
                    painter = painterResource(
                        if (!isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                    ),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                        .throttledClickable(500L) { isExpanded = !isExpanded },
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = EbbingTheme.colors.primaryDefault,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                } else {
                    EbbingTodoList(
                        listState = listState,
                        sortType = state.sortType,
                        selectedDate = selectedDate,
                        todoLists = state.schedulesByDateMap[selectedDate] ?: emptyList(),
                        schedulesByTodoInfo = state.schedulesByTodoInfo,
                        onAddTodoClick = { onAddTodoClick(selectedDate) },
                        onCheckedChange = onCheckedChange,
                        onSortTypeClick = onSortTypeClick,
                        onEditScheduleClick = onEditScheduleClick,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            EbbingCalendar(
                calendarState = calendarState,
                schedulesByDateMap = state.schedulesByDateMap,
                onDateSelect = {
                    if (selectedDate != it) {
                        scope.launch {
                            selectedDate = it
                            listState.animateScrollToItem(0)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f)
                    .padding(horizontal = 20.dp)
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = EbbingTheme.colors.primaryDefault,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                EbbingTodoList(
                    listState = listState,
                    sortType = state.sortType,
                    selectedDate = selectedDate,
                    todoLists = state.schedulesByDateMap[selectedDate] ?: emptyList(),
                    schedulesByTodoInfo = state.schedulesByTodoInfo,
                    onAddTodoClick = { onAddTodoClick(selectedDate) },
                    onCheckedChange = onCheckedChange,
                    onSortTypeClick = onSortTypeClick,
                    onEditScheduleClick = onEditScheduleClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun EbbingTodoList(
    listState: LazyListState,
    sortType: SortType,
    selectedDate: LocalDate,
    todoLists: List<TodoSchedule>,
    schedulesByTodoInfo: Map<Int, List<TodoSchedule>>,
    onCheckedChange: (TodoSchedule) -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    onAddTodoClick: () -> Unit,
    onSortTypeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            val displayDate = if (selectedDate == LocalDate.now()) "오늘"
            else "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일"

            Text(
                text = "${displayDate}  할 일 ${todoLists.size}",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.weight(1f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable { onSortTypeClick() },
            ) {
                Text(
                    text = sortType.displayName,
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black,
                )

                Image(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
                    modifier = Modifier.size(24.dp),
                )
            }

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = EbbingTheme.colors.background,
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(EbbingTheme.colors.primaryDefault)
                    .clickable { onAddTodoClick() },
            )
        }

        if (todoLists.isNotEmpty()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(
                    items = todoLists,
                    key = { item -> item.id },
                ) { item ->
                    TodoListCard(
                        todo = item,
                        todosWithSameInfo = schedulesByTodoInfo[item.infoId] ?: emptyList(),
                        onCheckedChange = onCheckedChange,
                        onEditScheduleClick = onEditScheduleClick,
                        modifier = Modifier
                            .padding(
                                horizontal = 20.dp,
                                vertical = 6.dp,
                            )
                            .animateItem()
                    )
                }

                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        } else {
            Text(
                text = "금일 스케줄이 없어요.\n우측 상단 + 버튼을 눌러 새로운 스케줄을 만들어보세요.",
                style = EbbingTheme.typography.bodySM,
                textAlign = TextAlign.Center,
                color = EbbingTheme.colors.dark3,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp),
            )
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            EbbingCheck(
                checked = todo.isDone,
                colorValue = todo.color,
                onCheckedChange = { onCheckedChange(todo) },
                modifier = Modifier.size(24.dp),
            )

            Text(
                text = todo.priority.toString(),
                style = EbbingTheme.typography.bodySSB,
                color = Color(todo.color),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

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
                colorFilter = ColorFilter.tint(EbbingTheme.colors.dark1),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onEditScheduleClick(todo) }
            )
        }
    }
}

@EbbingPreview
@Composable
private fun Preview1() {
    BasePreview {
        HomeScreen(
            workedDate = LocalDate.now(),
            state = HomeState(isLoading = false),
            onAddTodoClick = {},
            onCheckedChange = {},
            onEditScheduleClick = {},
            onSortTypeClick = {},
        )
    }
}
