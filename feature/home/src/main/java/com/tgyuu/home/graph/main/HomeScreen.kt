package com.tgyuu.home.graph.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.domain.model.CalendarDefaultView
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import com.tgyuu.home.graph.main.contract.HomeIntent.OnCheckChanged
import com.tgyuu.home.graph.main.contract.HomeIntent.OnSortTypeClick
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.home.graph.main.ui.EbbingTodoList
import com.tgyuu.home.graph.main.ui.bottomsheet.DelayBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.DeleteBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.OptionsBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.SortTypeBottomSheet
import com.tgyuu.home.graph.main.ui.bottomsheet.UpdateBottomSheet
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDelayAllDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDelayDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteMemoDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteRemainingDialog
import com.tgyuu.home.graph.main.ui.dialog.ConfirmDeleteSingleDialog
import com.tgyuu.home.graph.main.ui.dialog.DialogType
import com.tgyuu.home.graph.main.ui.dialog.DialogType.ConfirmDeleteRemaining
import com.tgyuu.home.graph.main.ui.dialog.DialogType.ConfirmDeleteSingle
import com.tgyuu.home.graph.main.ui.dialog.InAppReviewDialog
import com.tgyuu.home.graph.main.ui.dialog.WidgetNudgeDialog
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    workedDate: LocalDate,
    showWidgetNudge: Boolean = false,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val activity = LocalContext.current as? Activity
    var isShowDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.initCurrentMonthSchedules()
    }

    LaunchedEffect(showWidgetNudge) {
        if (showWidgetNudge) {
            viewModel.showWidgetNudgeDialog()
        }
    }

    HandleDialogs(
        isShowDialog = isShowDialog,
        dialogType = dialogType,
        onDismiss = { isShowDialog = false },
        onIntent = viewModel::onIntent,
    )

    AnimatedVisibility(state.showWidgetNudgeDialog) {
        WidgetNudgeDialog(
            onDismiss = { viewModel.onIntent(HomeIntent.OnWidgetNudgeDismiss) }
        )
    }

    if (state.showInAppReviewDialog) {
        InAppReviewDialog(
            onDismiss = { viewModel.dismissInAppReviewDialog() },
            onReviewClick = {
                viewModel.dismissInAppReviewDialog()
                scope.launch {
                    activity?.let { viewModel.inAppReviewManager.requestInAppReview(it) }
                }
            },
        )
    }

    HomeScreen(
        workedDate = workedDate,
        state = state,
        onCurrentDateChanged = { viewModel.onIntent(HomeIntent.OnCurrentDateChanged(it)) },
        onAddTodoClick = { viewModel.onIntent(OnAddTodoClick(it)) },
        onCheckedChange = { viewModel.onIntent(OnCheckChanged(it)) },
        onSyncClick = { viewModel.onIntent(HomeIntent.OnSyncClick) },
        onCalendarViewChanged = { viewModel.onIntent(HomeIntent.OnCalendarViewChanged(it)) },
        onSortTypeClick = {
            viewModel.onIntent(OnSortTypeClick({
                SortTypeBottomSheet(
                    originSortType = state.sortType,
                    onClickUpdate = { viewModel.onIntent(HomeIntent.OnUpdateSortType(it)) },
                )
            }))
        },
        onEditScheduleClick = { schedule ->
            viewModel.onIntent(
                HomeIntent.OnEditScheduleClick {
                    OptionsBottomSheet(
                        selectedSchedule = schedule,
                        onClickDelay = { delayedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(
                                    HomeIntent.OnDelayScheduleClick {
                                        DelayBottomSheet(
                                            selectedSchedule = delayedSchedule,
                                            onClickDelaySingle = {
                                                scope.launch {
                                                    viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                                    val (restDays, expectedDateExcludingRestDays) = viewModel.calculateDelayInfo(
                                                        delayedSchedule.infoId,
                                                        delayedSchedule.date
                                                    )
                                                    dialogType = DialogType.ConfirmDelay(
                                                        schedule = delayedSchedule,
                                                        restDays = restDays,
                                                        expectedDateExcludingRestDays = expectedDateExcludingRestDays,
                                                        expectedDateIncludingRestDays = delayedSchedule.date.plusDays(1)
                                                    )
                                                    isShowDialog = true
                                                }
                                            },
                                            onClickDelayAll = {
                                                scope.launch {
                                                    viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                                    val (restDays, expectedDateExcludingRestDays) = viewModel.calculateDelayInfo(
                                                        delayedSchedule.infoId,
                                                        delayedSchedule.date
                                                    )
                                                    dialogType = DialogType.ConfirmDelayAll(
                                                        schedule = delayedSchedule,
                                                        restDays = restDays,
                                                    )
                                                    isShowDialog = true
                                                }
                                            },
                                        )
                                    }
                                )
                            }
                        },
                        onClickDelete = { deletedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(
                                    HomeIntent.OnDeleteScheduleClick {
                                        DeleteBottomSheet(
                                            selectedSchedule = deletedSchedule,
                                            onClickDeleteSingle = {
                                                scope.launch {
                                                    viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                                    dialogType =
                                                        ConfirmDeleteSingle(deletedSchedule)
                                                    isShowDialog = true
                                                }
                                            },
                                            onClickDeleteRemaining = {
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
                        onClickUpdate = { updatedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(
                                    HomeIntent.OnUpdateScheduleClick {
                                        UpdateBottomSheet(
                                            selectedSchedule = updatedSchedule,
                                            onClickUpdateDate = {
                                                viewModel.onIntent(
                                                    HomeIntent.OnUpdateDateClick(
                                                        updatedSchedule
                                                    )
                                                )
                                            },
                                            onClickUpdateInfo = {
                                                viewModel.onIntent(
                                                    HomeIntent.OnUpdateInfoClick(
                                                        updatedSchedule
                                                    )
                                                )
                                            },
                                        )
                                    }
                                )
                            }
                        },
                        onClickMemo = { selectedSchedule ->
                            viewModel.onIntent(HomeIntent.OnMemoClick(selectedSchedule))
                        },
                        onClickDeleteMemo = { selectedSchedule ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                dialogType = DialogType.ConfirmDeleteMemo(selectedSchedule)
                                isShowDialog = true
                            }
                        }
                    )
                }
            )
        },
    )
}

@Composable
private fun HomeScreen(
    workedDate: LocalDate,
    state: HomeState,
    onAddTodoClick: (LocalDate) -> Unit,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onSortTypeClick: () -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onSyncClick: () -> Unit,
    onCurrentDateChanged: (LocalDate) -> Unit,
    onCalendarViewChanged: (CalendarDefaultView) -> Unit,
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
            onCurrentDateChanged = onCurrentDateChanged,
            onSyncClick = onSyncClick,
            onCalendarViewChanged = onCalendarViewChanged,
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
            onCurrentDateChanged = onCurrentDateChanged,
            onSyncClick = onSyncClick,
            modifier = modifier
        )
    }
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
    onCalendarViewChanged: (CalendarDefaultView) -> Unit,
    modifier: Modifier = Modifier
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val localDensity = LocalDensity.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var selectedDate by remember(workedDate) { mutableStateOf(workedDate) }
    val calendarState = rememberCalendarState()
    var monthlyCalendarHeight by remember { mutableStateOf(1000.dp) }
    var weeklyCalendarHeight by remember { mutableStateOf(0.dp) }
    val monthlyCalendarHeightPx = with(localDensity) { monthlyCalendarHeight.toPx() }
    val weeklyCalendarHeightPx = with(localDensity) { weeklyCalendarHeight.toPx() }
    val offsetAnimatable = remember { Animatable(monthlyCalendarHeightPx) }
    val animatedTopPadding = with(localDensity) { offsetAnimatable.value.toDp() }
    val isCollapsed = offsetAnimatable.value <
        (weeklyCalendarHeightPx.takeIf { it > 0f } ?: monthlyCalendarHeightPx) / 2

    // calendarDefaultView 변경 시 (DataStore 로딩 완료 포함) offset 동기화
    LaunchedEffect(state.calendarDefaultView, monthlyCalendarHeightPx, weeklyCalendarHeightPx) {
        when (state.calendarDefaultView) {
            CalendarDefaultView.DAILY -> offsetAnimatable.snapTo(0f)
            CalendarDefaultView.WEEKLY -> {
                if (weeklyCalendarHeightPx > 0f) {
                    offsetAnimatable.snapTo(weeklyCalendarHeightPx)
                }
            }
            CalendarDefaultView.MONTHLY -> {
                if (monthlyCalendarHeightPx > 0f) {
                    offsetAnimatable.snapTo(monthlyCalendarHeightPx)
                }
            }
        }
    }

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
                    val height = with(localDensity) { coordinates.size.height.toDp() }
                    if (state.showWeekOnly) weeklyCalendarHeight = height
                    else monthlyCalendarHeight = height
                }
        ) {
            EbbingCalendar(
                calendarState = calendarState,
                schedulesByDateMap = state.schedulesByDateMap,
                startFromMonday = state.mondayStart,
                showWeekOnly = state.showWeekOnly,
                onSelectDate = {
                    if (selectedDate != it) {
                        scope.launch {
                            selectedDate = it
                            listState.animateScrollToItem(0)
                        }
                    }
                },
                onGotoTodayClick = {
                    analyticsHelper.logEvent(
                        AnalyticsEvent.Click(screenName = "Home", buttonName = "ReturnToToday")
                    )
                },
                onSyncClick = onSyncClick,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(
                thickness = 8.dp,
                color = EbbingTheme.colors.fillTextfield,
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
                    if (isCollapsed) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
                    .pointerInput(monthlyCalendarHeightPx, weeklyCalendarHeightPx) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val snapTarget = snapToClosestOf(
                                        value = offsetAnimatable.value,
                                        candidates = buildList {
                                            add(0f)
                                            if (weeklyCalendarHeightPx > 0f) add(weeklyCalendarHeightPx)
                                            add(monthlyCalendarHeightPx)
                                        },
                                    )
                                    when (snapTarget) {
                                        0f -> {
                                            onCalendarViewChanged(CalendarDefaultView.DAILY)
                                            offsetAnimatable.animateTo(0f, animationSpec = spring())
                                        }
                                        weeklyCalendarHeightPx -> {
                                            onCalendarViewChanged(CalendarDefaultView.WEEKLY)
                                            offsetAnimatable.animateTo(weeklyCalendarHeightPx, animationSpec = spring())
                                        }
                                        else -> {
                                            onCalendarViewChanged(CalendarDefaultView.MONTHLY)
                                            offsetAnimatable.animateTo(monthlyCalendarHeightPx, animationSpec = spring())
                                        }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    val newValue = (offsetAnimatable.value + dragAmount.y)
                                        .coerceIn(0f, monthlyCalendarHeightPx)
                                    offsetAnimatable.snapTo(newValue)
                                }
                            }
                        )
                    }
                    .throttledClickable(500L) {
                        analyticsHelper.logEvent(
                            AnalyticsEvent.Click(
                                screenName = "Home",
                                buttonName = when {
                                    isCollapsed -> "FoldList"
                                    state.showWeekOnly -> "ExpandList"
                                    else -> "SwitchToWeekly"
                                },
                            )
                        )
                        when {
                            isCollapsed -> { // COLLAPSED → MONTHLY
                                scope.launch {
                                    offsetAnimatable.animateTo(monthlyCalendarHeightPx, animationSpec = spring())
                                }
                                onCalendarViewChanged(CalendarDefaultView.MONTHLY)
                            }
                            state.showWeekOnly -> { // WEEKLY → COLLAPSED
                                onCalendarViewChanged(CalendarDefaultView.DAILY)
                                scope.launch {
                                    offsetAnimatable.animateTo(0f, animationSpec = spring())
                                }
                            }
                            else -> { // MONTHLY → WEEKLY
                                scope.launch {
                                    if (weeklyCalendarHeightPx > 0f) {
                                        offsetAnimatable.animateTo(weeklyCalendarHeightPx, animationSpec = spring())
                                    }
                                }
                                onCalendarViewChanged(CalendarDefaultView.WEEKLY)
                            }
                        }
                    },
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = EbbingTheme.colors.primaryNormal,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                EbbingTodoList(
                    sortType = state.sortType,
                    selectedDate = selectedDate,
                    todoLists = state.schedulesByDateMap[selectedDate] ?: emptyList(),
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
    val analyticsHelper = LocalAnalyticsHelper.current
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
            startFromMonday = state.mondayStart,
            onSelectDate = {
                if (selectedDate != it) {
                    scope.launch {
                        selectedDate = it
                    }
                }
            },
            onGotoTodayClick = {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "Home", buttonName = "ReturnToToday")
                )
            },
            onSyncClick = onSyncClick,
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
                    color = EbbingTheme.colors.primaryNormal,
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            EbbingTodoList(
                sortType = state.sortType,
                selectedDate = selectedDate,
                todoLists = state.schedulesByDateMap[selectedDate] ?: emptyList(),
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
    val analyticsHelper = LocalAnalyticsHelper.current

    LaunchedEffect(dialogType) {
        if (dialogType != null) {
            val dialogTypeName = when (dialogType) {
                is ConfirmDeleteSingle -> "confirm_delete_single"
                is ConfirmDeleteRemaining -> "confirm_delete_remaining"
                is DialogType.ConfirmDelay -> "confirm_delay"
                is DialogType.ConfirmDelayAll -> "confirm_delay_all"
                is DialogType.ConfirmDeleteMemo -> "confirm_delete_memo"
            }

            analyticsHelper.logEvent(
                AnalyticsEvent.Action(
                    screenName = "Home",
                    actionName = "show_dialog",
                    properties = mapOf("dialog_type" to dialogTypeName),
                )
            )
        }
    }

    if (isShowDialog && dialogType != null) {
        when (val dt = dialogType) {
            is ConfirmDeleteSingle -> ConfirmDeleteSingleDialog(
                schedule = dt.schedule,
                analyticsHelper = analyticsHelper,
                onDismissRequest = onDismiss,
                onDeleteClick = {
                    onDismiss()
                    onIntent(HomeIntent.OnDeleteSingleClick(dt.schedule))
                },
            )

            is ConfirmDeleteRemaining -> ConfirmDeleteRemainingDialog(
                schedule = dt.schedule,
                analyticsHelper = analyticsHelper,
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

            else -> Unit
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
            onCurrentDateChanged = {},
            onSortTypeClick = {},
            onSyncClick = {},
            onCalendarViewChanged = {},
        )
    }
}

private fun snapToClosestOf(value: Float, candidates: List<Float>): Float =
    candidates.minByOrNull { kotlin.math.abs(it - value) } ?: value
