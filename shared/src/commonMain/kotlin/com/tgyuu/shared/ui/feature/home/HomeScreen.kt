package com.tgyuu.shared.ui.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.now
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.shared.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.home.bottomsheet.DelayBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.DeleteBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.OptionsBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.SortTypeBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.UpdateBottomSheet
import com.tgyuu.shared.ui.feature.home.component.EbbingTodoList
import com.tgyuu.shared.ui.feature.home.dialog.ConfirmDelayAllDialog
import com.tgyuu.shared.ui.feature.home.dialog.ConfirmDelayDialog
import com.tgyuu.shared.ui.feature.home.dialog.ConfirmDeleteMemoDialog
import com.tgyuu.shared.ui.feature.home.dialog.ConfirmDeleteRemainingDialog
import com.tgyuu.shared.ui.feature.home.dialog.ConfirmDeleteSingleDialog
import com.tgyuu.shared.ui.feature.home.dialog.DialogType
import com.tgyuu.shared.ui.feature.home.dialog.WidgetNudgeDialog
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var isShowDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    val workedDate = state.selectedDate ?: LocalDate.now()

    // Set up ViewModel callbacks
    LaunchedEffect(viewModel) {
        viewModel.onShowSortTypeBottomSheet = {
            scope.launch {
                bottomSheetState.setBottomSheetContent {
                    SortTypeBottomSheet(
                        originSortType = state.sortType,
                        onClickUpdate = { sortType ->
                            viewModel.onIntent(HomeIntent.OnUpdateSortType(sortType))
                            scope.launch { bottomSheetState.hide() }
                        },
                    )
                }
                bottomSheetState.show()
            }
        }

        viewModel.onShowEditOptionsBottomSheet = { schedule ->
            scope.launch {
                showOptionsBottomSheet(
                    schedule = schedule,
                    bottomSheetState = bottomSheetState,
                    viewModel = viewModel,
                    onShowDialog = { type ->
                        dialogType = type
                        isShowDialog = true
                    },
                )
            }
        }
    }

    // Handle Dialogs
    HandleDialogs(
        isShowDialog = isShowDialog,
        dialogType = dialogType,
        onDismiss = { isShowDialog = false },
        onIntent = viewModel::onIntent,
    )

    // Widget Nudge Dialog
    AnimatedVisibility(state.showWidgetNudgeDialog) {
        WidgetNudgeDialog(
            onDismiss = { viewModel.onIntent(HomeIntent.OnWidgetNudgeDismiss) }
        )
    }

    // Bottom Sheet
    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { scope.launch { bottomSheetState.hide() } },
    )

    // Main Content
    if (isTablet) {
        TabletHomeScreen(
            workedDate = workedDate,
            state = state,
            onAddTodoClick = { viewModel.onIntent(HomeIntent.OnAddTodoClick(it)) },
            onCheckedChange = { viewModel.onIntent(HomeIntent.OnCheckChanged(it)) },
            onSortTypeClick = { viewModel.onIntent(HomeIntent.OnSortTypeClick) },
            onEditScheduleClick = { viewModel.onIntent(HomeIntent.OnEditScheduleClick(it)) },
            onCurrentDateChanged = { viewModel.onIntent(HomeIntent.OnCurrentDateChanged(it)) },
            onSyncClick = { viewModel.onIntent(HomeIntent.OnSyncClick) },
            modifier = modifier,
        )
    } else {
        PhoneHomeScreen(
            workedDate = workedDate,
            state = state,
            onAddTodoClick = { viewModel.onIntent(HomeIntent.OnAddTodoClick(it)) },
            onCheckedChange = { viewModel.onIntent(HomeIntent.OnCheckChanged(it)) },
            onSortTypeClick = { viewModel.onIntent(HomeIntent.OnSortTypeClick) },
            onEditScheduleClick = { viewModel.onIntent(HomeIntent.OnEditScheduleClick(it)) },
            onCurrentDateChanged = { viewModel.onIntent(HomeIntent.OnCurrentDateChanged(it)) },
            onSyncClick = { viewModel.onIntent(HomeIntent.OnSyncClick) },
            modifier = modifier,
        )
    }
}

private suspend fun showOptionsBottomSheet(
    schedule: TodoScheduleUiModel,
    bottomSheetState: EbbingBottomSheetState,
    viewModel: HomeViewModel,
    onShowDialog: (DialogType) -> Unit,
) {
    bottomSheetState.setBottomSheetContent {
        OptionsBottomSheet(
            selectedSchedule = schedule,
            onClickUpdate = { updatedSchedule ->
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showUpdateBottomSheet(
                        schedule = updatedSchedule,
                        bottomSheetState = bottomSheetState,
                        viewModel = viewModel,
                    )
                }
            },
            onClickDelete = { deletedSchedule ->
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showDeleteBottomSheet(
                        schedule = deletedSchedule,
                        bottomSheetState = bottomSheetState,
                        onShowDialog = onShowDialog,
                    )
                }
            },
            onClickDelay = { delayedSchedule ->
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showDelayBottomSheet(
                        schedule = delayedSchedule,
                        bottomSheetState = bottomSheetState,
                        viewModel = viewModel,
                        onShowDialog = onShowDialog,
                    )
                }
            },
            onClickMemo = { selectedSchedule ->
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                }
                viewModel.onIntent(HomeIntent.OnMemoClick(selectedSchedule))
            },
            onClickDeleteMemo = { selectedSchedule ->
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    onShowDialog(DialogType.ConfirmDeleteMemo(selectedSchedule))
                }
            },
        )
    }
    bottomSheetState.show()
}

private suspend fun showUpdateBottomSheet(
    schedule: TodoScheduleUiModel,
    bottomSheetState: EbbingBottomSheetState,
    viewModel: HomeViewModel,
) {
    bottomSheetState.setBottomSheetContent {
        UpdateBottomSheet(
            selectedSchedule = schedule,
            onClickUpdateDate = {
                kotlinx.coroutines.GlobalScope.launch { bottomSheetState.hide() }
                viewModel.onIntent(HomeIntent.OnUpdateDateClick(schedule))
            },
            onClickUpdateInfo = {
                kotlinx.coroutines.GlobalScope.launch { bottomSheetState.hide() }
                viewModel.onIntent(HomeIntent.OnUpdateInfoClick(schedule))
            },
        )
    }
    bottomSheetState.show()
}

private suspend fun showDeleteBottomSheet(
    schedule: TodoScheduleUiModel,
    bottomSheetState: EbbingBottomSheetState,
    onShowDialog: (DialogType) -> Unit,
) {
    bottomSheetState.setBottomSheetContent {
        DeleteBottomSheet(
            selectedSchedule = schedule,
            onClickDeleteSingle = {
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    onShowDialog(DialogType.ConfirmDeleteSingle(schedule))
                }
            },
            onClickDeleteRemaining = {
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    onShowDialog(DialogType.ConfirmDeleteRemaining(schedule))
                }
            },
        )
    }
    bottomSheetState.show()
}

private suspend fun showDelayBottomSheet(
    schedule: TodoScheduleUiModel,
    bottomSheetState: EbbingBottomSheetState,
    viewModel: HomeViewModel,
    onShowDialog: (DialogType) -> Unit,
) {
    bottomSheetState.setBottomSheetContent {
        DelayBottomSheet(
            selectedSchedule = schedule,
            onClickDelaySingle = {
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    val (_, expectedDateExcludingRestDays) = viewModel.calculateDelayInfo(
                        schedule.infoId,
                        schedule.date
                    )
                    onShowDialog(
                        DialogType.ConfirmDelay(
                            schedule = schedule,
                            restDays = emptySet(),
                            expectedDateExcludingRestDays = expectedDateExcludingRestDays,
                            expectedDateIncludingRestDays = schedule.date.plus(1, DateTimeUnit.DAY)
                        )
                    )
                }
            },
            onClickDelayAll = {
                kotlinx.coroutines.GlobalScope.launch {
                    bottomSheetState.hide()
                    onShowDialog(
                        DialogType.ConfirmDelayAll(
                            schedule = schedule,
                            restDays = emptySet(),
                        )
                    )
                }
            },
        )
    }
    bottomSheetState.show()
}

@Composable
private fun PhoneHomeScreen(
    workedDate: LocalDate,
    state: HomeState,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onSortTypeClick: () -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onCurrentDateChanged: (LocalDate) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val localDensity = LocalDensity.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var selectedDate by remember(workedDate) { mutableStateOf(workedDate) }
    val calendarState = rememberCalendarState()
    var isExpanded by remember { mutableStateOf(false) }
    var calendarHeight by remember { mutableStateOf(1000.dp) }
    val animatedTopPadding by animateDpAsState(targetValue = if (isExpanded) 0.dp else calendarHeight)

    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(workedDate)
    }

    LaunchedEffect(calendarState.currentDisplayDate.month) {
        onCurrentDateChanged(calendarState.currentDisplayDate)
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
                onSelectDate = {
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
            Icon(
                imageVector = if (!isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = EbbingTheme.colors.black,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { isExpanded = !isExpanded },
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
                    sortType = state.sortType,
                    selectedDate = selectedDate,
                    todoLists = state.schedulesByDateMap[selectedDate]?.toList() ?: emptyList(),
                    schedulesByTodoInfo = state.schedulesByTodoInfo,
                    onSelectDate = {
                        if (selectedDate != it) {
                            scope.launch {
                                selectedDate = it
                                calendarState.onDateSelect(it)
                                listState.animateScrollToItem(0)
                            }
                        }
                    },
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
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onSortTypeClick: () -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onCurrentDateChanged: (LocalDate) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedDate by remember(workedDate) { mutableStateOf(workedDate) }
    val calendarState = rememberCalendarState()

    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(workedDate)
    }

    LaunchedEffect(calendarState.currentDisplayDate.month) {
        onCurrentDateChanged(calendarState.currentDisplayDate)
    }

    Row(modifier = modifier.fillMaxSize()) {
        EbbingCalendar(
            calendarState = calendarState,
            schedulesByDateMap = state.schedulesByDateMap,
            onSelectDate = {
                if (selectedDate != it) {
                    scope.launch {
                        selectedDate = it
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
                sortType = state.sortType,
                selectedDate = selectedDate,
                todoLists = state.schedulesByDateMap[selectedDate]?.toList() ?: emptyList(),
                schedulesByTodoInfo = state.schedulesByTodoInfo,
                onSelectDate = {
                    if (selectedDate != it) {
                        scope.launch {
                            selectedDate = it
                            calendarState.onDateSelect(it)
                        }
                    }
                },
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

@Composable
private fun HandleDialogs(
    isShowDialog: Boolean,
    dialogType: DialogType?,
    onDismiss: () -> Unit,
    onIntent: (HomeIntent) -> Unit,
) {
    val dt = dialogType
    if (isShowDialog && dt != null) {
        when (dt) {
            is DialogType.ConfirmDeleteSingle -> ConfirmDeleteSingleDialog(
                schedule = dt.schedule,
                onDismissRequest = onDismiss,
                onDeleteClick = {
                    onDismiss()
                    onIntent(HomeIntent.OnDeleteSingleClick(dt.schedule))
                },
            )

            is DialogType.ConfirmDeleteRemaining -> ConfirmDeleteRemainingDialog(
                schedule = dt.schedule,
                onDismissRequest = onDismiss,
                onDeleteClick = {
                    onDismiss()
                    onIntent(HomeIntent.OnDeleteRemainingClick(dt.schedule))
                },
            )

            is DialogType.ConfirmDelay -> ConfirmDelayDialog(
                schedule = dt.schedule,
                restDays = dt.restDays,
                expectedDateExcludingRestDays = dt.expectedDateExcludingRestDays,
                expectedDateIncludingRestDays = dt.expectedDateIncludingRestDays,
                onDismissRequest = onDismiss,
                onDelayClick = { includeRestDays ->
                    onDismiss()
                    onIntent(HomeIntent.OnDelaySingleClick(dt.schedule, includeRestDays))
                },
            )

            is DialogType.ConfirmDelayAll -> ConfirmDelayAllDialog(
                schedule = dt.schedule,
                restDays = dt.restDays,
                onDismissRequest = onDismiss,
                onDelayClick = { includeRestDays ->
                    onDismiss()
                    onIntent(HomeIntent.OnDelayAllClick(dt.schedule, includeRestDays))
                },
            )

            is DialogType.ConfirmDeleteMemo -> ConfirmDeleteMemoDialog(
                schedule = dt.schedule,
                onDismissRequest = onDismiss,
                onDeleteClick = {
                    onDismiss()
                    onIntent(HomeIntent.OnDeleteMemoClick(dt.schedule))
                },
            )
        }
    }
}
