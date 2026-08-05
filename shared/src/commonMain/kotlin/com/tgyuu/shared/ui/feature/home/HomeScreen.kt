package com.tgyuu.shared.ui.feature.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.now
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toLocalDateOrThrow
import com.tgyuu.shared.designsystem.component.calendar.CalendarState
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.shared.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.util.throttledClickable
import com.tgyuu.shared.domain.model.CalendarDefaultView
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.ui.feature.home.bottomsheet.DelayBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.DeleteBottomSheet
import com.tgyuu.shared.ui.feature.home.bottomsheet.OptionsBottomSheet
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
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_review_button
import ebbingplanner.shared.generated.resources.home_review_sub
import ebbingplanner.shared.generated.resources.home_review_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    onRequestInAppReview: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var isShowDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    if (state.showInAppReviewDialog) {
        EbbingDialog(
            dialogTop = {
                EbbingDialogDefaultTop(
                    title = stringResource(Res.string.home_review_title),
                    subText = stringResource(Res.string.home_review_sub),
                )
            },
            dialogBottom = {
                EbbingSolidButton(
                    label = stringResource(Res.string.home_review_button),
                    onClick = {
                        viewModel.dismissInAppReviewDialog()
                        onRequestInAppReview()
                    },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                )
            },
            onDismissRequest = { viewModel.dismissInAppReviewDialog() },
        )
    }

    val workedDate = state.selectedDate ?: LocalDate.now()

    // Set up ViewModel callbacks
    LaunchedEffect(viewModel) {
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
                    scope = this,
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
            onSortTypeChange = { viewModel.onIntent(HomeIntent.OnUpdateSortType(it)) },
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
            onSortTypeChange = { viewModel.onIntent(HomeIntent.OnUpdateSortType(it)) },
            onEditScheduleClick = { viewModel.onIntent(HomeIntent.OnEditScheduleClick(it)) },
            onCurrentDateChanged = { viewModel.onIntent(HomeIntent.OnCurrentDateChanged(it)) },
            onCalendarViewChanged = { viewModel.onIntent(HomeIntent.OnCalendarViewChanged(it)) },
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
    scope: CoroutineScope,
) {
    bottomSheetState.setBottomSheetContent {
        OptionsBottomSheet(
            selectedSchedule = schedule,
            onClickUpdate = { updatedSchedule ->
                scope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showUpdateBottomSheet(
                        schedule = updatedSchedule,
                        bottomSheetState = bottomSheetState,
                        viewModel = viewModel,
                        scope = scope,
                    )
                }
            },
            onClickDelete = { deletedSchedule ->
                scope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showDeleteBottomSheet(
                        schedule = deletedSchedule,
                        bottomSheetState = bottomSheetState,
                        onShowDialog = onShowDialog,
                        scope = scope,
                    )
                }
            },
            onClickDelay = { delayedSchedule ->
                scope.launch {
                    bottomSheetState.hide()
                    delay(200L)
                    showDelayBottomSheet(
                        schedule = delayedSchedule,
                        bottomSheetState = bottomSheetState,
                        viewModel = viewModel,
                        onShowDialog = onShowDialog,
                        scope = scope,
                    )
                }
            },
            onClickMemo = { selectedSchedule ->
                scope.launch {
                    bottomSheetState.hide()
                }
                viewModel.onIntent(HomeIntent.OnMemoClick(selectedSchedule))
            },
            onClickDeleteMemo = { selectedSchedule ->
                scope.launch {
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
    scope: CoroutineScope,
) {
    bottomSheetState.setBottomSheetContent {
        UpdateBottomSheet(
            selectedSchedule = schedule,
            onClickUpdateDate = {
                scope.launch { bottomSheetState.hide() }
                viewModel.onIntent(HomeIntent.OnUpdateDateClick(schedule))
            },
            onClickUpdateInfo = {
                scope.launch { bottomSheetState.hide() }
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
    scope: CoroutineScope,
) {
    bottomSheetState.setBottomSheetContent {
        DeleteBottomSheet(
            selectedSchedule = schedule,
            onClickDeleteSingle = {
                scope.launch {
                    bottomSheetState.hide()
                    onShowDialog(DialogType.ConfirmDeleteSingle(schedule))
                }
            },
            onClickDeleteRemaining = {
                scope.launch {
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
    scope: CoroutineScope,
) {
    bottomSheetState.setBottomSheetContent {
        DelayBottomSheet(
            selectedSchedule = schedule,
            onClickDelaySingle = {
                scope.launch {
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
                scope.launch {
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
    onSortTypeChange: (SortType) -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onCurrentDateChanged: (LocalDate) -> Unit,
    onCalendarViewChanged: (CalendarDefaultView) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val calendarState = rememberCalendarState()
    var selectedDate by rememberRestoredSelectedDate(workedDate, calendarState)

    LaunchedEffect(calendarState.currentDisplayDate.month) {
        onCurrentDateChanged(calendarState.currentDisplayDate)
    }

    // 아래 리스트 스크롤로 월/주 전환 (위로 스크롤 → 주간, 최상단에서 당기면 → 월간)
    val currentShowWeekOnly by rememberUpdatedState(state.showWeekOnly)
    val currentOnCalendarViewChanged by rememberUpdatedState(onCalendarViewChanged)
    val calendarNestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -4f && !currentShowWeekOnly) {
                    currentOnCalendarViewChanged(CalendarDefaultView.WEEKLY)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (available.y > 4f && currentShowWeekOnly) {
                    currentOnCalendarViewChanged(CalendarDefaultView.MONTHLY)
                }
                return Offset.Zero
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EbbingCalendar(
                calendarState = calendarState,
                schedulesByDateMap = state.schedulesByDateMap,
                startFromMonday = state.mondayStart,
                showWeekOnly = state.showWeekOnly,
                showViewToggle = true,
                onSelectDate = {
                    if (selectedDate != it) {
                        scope.launch {
                            selectedDate = it
                            listState.animateScrollToItem(0)
                        }
                    }
                },
                onSyncClick = onSyncClick,
                onViewToggle = { isWeek ->
                    onCalendarViewChanged(
                        if (isWeek) CalendarDefaultView.WEEKLY else CalendarDefaultView.MONTHLY
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 20.dp)
                    .padding(horizontal = 20.dp),
            )

            HorizontalDivider(
                thickness = 8.dp,
                color = EbbingTheme.colors.light3,
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
                    onCheckedChange = onCheckedChange,
                    onSortTypeChange = onSortTypeChange,
                    onEditScheduleClick = onEditScheduleClick,
                    calendarNestedScroll = calendarNestedScroll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }

        AddTodoFab(
            onClick = { onAddTodoClick(selectedDate) },
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun AddTodoFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(end = 20.dp, bottom = 20.dp)
            .size(52.dp)
            .clip(CircleShape)
            .background(EbbingTheme.colors.primaryDefault)
            .throttledClickable(500L) { onClick() },
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = EbbingTheme.colors.white,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun TabletHomeScreen(
    workedDate: LocalDate,
    state: HomeState,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onCurrentDateChanged: (LocalDate) -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val calendarState = rememberCalendarState()
    var selectedDate by rememberRestoredSelectedDate(workedDate, calendarState)

    LaunchedEffect(calendarState.currentDisplayDate.month) {
        onCurrentDateChanged(calendarState.currentDisplayDate)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            EbbingCalendar(
                calendarState = calendarState,
                schedulesByDateMap = state.schedulesByDateMap,
                startFromMonday = state.mondayStart,
                onSelectDate = {
                    if (selectedDate != it) {
                        scope.launch {
                            selectedDate = it
                        }
                    }
                },
                onSyncClick = onSyncClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f)
                    .padding(horizontal = 20.dp),
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
                    onCheckedChange = onCheckedChange,
                    onSortTypeChange = onSortTypeChange,
                    onEditScheduleClick = onEditScheduleClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                )
            }
        }

        AddTodoFab(
            onClick = { onAddTodoClick(selectedDate) },
            modifier = Modifier.align(Alignment.BottomEnd),
        )
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

/** 화면 이동 후 복귀 시 선택 날짜를 복원하기 위한 LocalDate Saver */
private val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toFormattedString() },
    restore = { it.toLocalDateOrThrow() },
)

/**
 * 화면 이동 후 복귀 시 선택 날짜를 유지하고 캘린더에 복원한다.
 * Phone/Tablet 레이아웃이 공유한다.
 */
@Composable
private fun rememberRestoredSelectedDate(
    workedDate: LocalDate,
    calendarState: CalendarState,
): MutableState<LocalDate> {
    val selectedDate = rememberSaveable(workedDate, stateSaver = LocalDateSaver) {
        mutableStateOf(workedDate)
    }
    LaunchedEffect(workedDate) {
        calendarState.onDateSelect(selectedDate.value)
    }
    return selectedDate
}
