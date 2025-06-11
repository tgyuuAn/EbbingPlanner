package com.tgyuu.home.graph.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import com.tgyuu.home.graph.main.contract.HomeIntent.OnCheckedChange
import com.tgyuu.home.graph.main.contract.HomeIntent.OnSortTypeClick
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.home.graph.main.ui.EbbingTodoList
import com.tgyuu.home.graph.main.ui.bottomsheet.DeleteBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.EditScheduleBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.SortTypeBottomSheet
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDelayDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteMemoDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteRemainingDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteSingleDialog
import com.tgyuu.home.graph.main.ui.dialog.DialogType
import com.tgyuu.home.graph.main.ui.dialog.DialogType.ConfirmDeleteRemaining
import com.tgyuu.home.graph.main.ui.dialog.DialogType.ConfirmDeleteSingle
import kotlinx.coroutines.delay
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
            is ConfirmDeleteSingle -> ConfirmDeleteSingleDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDeleteClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDeleteSingleClick(dt.schedule))
                },
            )

            is ConfirmDeleteRemaining -> ConfirmDeleteRemainingDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDeleteClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDeleteRemainingClick(dt.schedule))
                },
            )

            is DialogType.ConfirmDelay -> ConfirmDelayDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDelayClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDelayScheduleClick(dt.schedule))
                },
            )

            is DialogType.ConfirmDeleteMemo -> ConfirmDeleteMemoDialog(
                schedule = dt.schedule,
                onDismissRequest = { isShowDialog = false },
                onDeleteClick = {
                    isShowDialog = false
                    viewModel.onIntent(HomeIntent.OnDeleteMemoClick(dt.schedule))
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
                                dialogType = DialogType.ConfirmDelay(delayedSchedule)
                                isShowDialog = true
                            }
                        },
                        onDeleteClick = { deletedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                delay(200L)
                                viewModel.onIntent(
                                    HomeIntent.OnDeleteScheduleClick {
                                        DeleteBottomSheet(
                                            selectedSchedule = deletedSchedule,
                                            onDeleteSingleClick = {
                                                scope.launch {
                                                    viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                                    dialogType =
                                                        ConfirmDeleteSingle(deletedSchedule)
                                                    isShowDialog = true
                                                }
                                            },
                                            onDeleteRemainingClick = {
                                                scope.launch {
                                                    viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                                    dialogType =
                                                        ConfirmDeleteRemaining(deletedSchedule)
                                                    isShowDialog = true
                                                }
                                            },
                                        )
                                    }
                                )
                            }
                        },
                        onUpdateClick = { updatedSchedule ->
                            viewModel.onIntent(HomeIntent.OnUpdateScheduleClick(updatedSchedule))
                        },
                        onMemoClick = { selectedSchedule ->
                            viewModel.onIntent(HomeIntent.OnMemoClick(selectedSchedule))
                        },
                        onDeleteMemoClick = { selectedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                dialogType = DialogType.ConfirmDeleteMemo(selectedSchedule)
                                isShowDialog = true
                            }
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
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneHomeScreen(
            workedDate = workedDate,
            state = state,
            onAddTodoClick = onAddTodoClick,
            onCheckedChange = onCheckedChange,
            onSortTypeClick = onSortTypeClick,
            onEditScheduleClick = onEditScheduleClick,
            modifier = modifier
        )
    } else {
        TabletHomeScreen(
            workedDate = workedDate,
            state = state,
            onAddTodoClick = onAddTodoClick,
            onCheckedChange = onCheckedChange,
            onSortTypeClick = onSortTypeClick,
            onEditScheduleClick = onEditScheduleClick,
            modifier = modifier
        )
    }
}

@Composable
private fun PhoneHomeScreen(
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

    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(workedDate)
    }

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
}

@Composable
private fun TabletHomeScreen(
    workedDate: LocalDate,
    state: HomeState,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoSchedule) -> Unit,
    onSortTypeClick: () -> Unit,
    onEditScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var selectedDate by remember(workedDate) { mutableStateOf(workedDate) }
    val calendarState = rememberCalendarState()

    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(workedDate)
    }

    Row(modifier = modifier.fillMaxSize()) {
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
