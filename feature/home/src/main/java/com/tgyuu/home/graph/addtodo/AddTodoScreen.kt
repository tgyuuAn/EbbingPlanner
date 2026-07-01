package com.tgyuu.home.graph.addtodo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.EbbingPageTransitionAnimation
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingColors
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.graph.ui.bottomsheet.RepeatCycleBottomSheet
import com.tgyuu.home.graph.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.ui.bottomsheet.TagBottomSheet
import com.tgyuu.home.graph.ui.dialog.ConfirmExitDialog
import com.tgyuu.home.graph.notification.NotificationScreen
import com.tgyuu.home.graph.ui.PriorityContent
import com.tgyuu.home.graph.ui.RepeatCycleContent
import com.tgyuu.home.graph.ui.RestDayContent
import com.tgyuu.home.graph.ui.ScheduleContent
import com.tgyuu.home.graph.ui.TagContent
import com.tgyuu.home.graph.ui.TitleContent
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
internal fun AddTodoRoute(
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var repeatCycleSheetKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewModel) {
        viewModel.loadNewTag()
        viewModel.loadNewRepeatCycle()
        viewModel.loadTags()
        viewModel.loadRepeatCycles()
    }

    EbbingPageTransitionAnimation(state.page) { page ->
        when (page) {
            AddTodoState.Page.ADD_TODO -> AddTodoScreen(
                state = state,
                onBackClick = { viewModel.onIntent(AddTodoIntent.OnBackClick) },
                onSelectedDateChangeClick = {
                    viewModel.onIntent(
                        AddTodoIntent.OnSelectedDataChangeClick(
                            {
                                SelectedDateBottomSheet(
                                    originSelectedDate = state.selectedDate,
                                    schedulesByDateMap = emptyMap(),
                                    startFromMonday = state.mondayStart,
                                    updateSelectedDate = {
                                        viewModel.onIntent(
                                            AddTodoIntent.OnSelectedDateChange(it)
                                        )
                                    },
                                )
                            }
                        )
                    )
                },
                onTitleChange = { viewModel.onIntent(AddTodoIntent.OnTitleChange(it)) },
                onPriorityChange = { viewModel.onIntent(AddTodoIntent.OnPriorityChange(it)) },
                onTagDropDownClick = {
                    viewModel.onIntent(
                        AddTodoIntent.OnTagDropDownClick(
                            {
                                TagBottomSheet(
                                    originTag = state.tag,
                                    tagList = state.tagList,
                                    updateTag = { viewModel.onIntent(AddTodoIntent.OnTagChange(it)) },
                                    onAddTagClick = { viewModel.onIntent(AddTodoIntent.OnAddTagClick) },
                                )
                            }
                        )
                    )
                },
                onRepeatCycleDropDownClick = {
                    repeatCycleSheetKey++
                    viewModel.onIntent(
                        AddTodoIntent.OnRepeatCycleDropDownClick(
                            {
                                RepeatCycleBottomSheet(
                                    repeatCycleList = state.repeatCycleList,
                                    originRepeatCycle = state.repeatCycle,
                                    selectedDate = state.selectedDate,
                                    openKey = repeatCycleSheetKey,
                                    startFromMonday = state.mondayStart,
                                    onAddRepeatCycleClick = {
                                        viewModel.onIntent(AddTodoIntent.OnAddRepeatCycleClick)
                                    },
                                    updateRepeatCycle = {
                                        viewModel.onIntent(
                                            AddTodoIntent.OnRepeatCycleChange(it)
                                        )
                                    },
                                )
                            }
                        )
                    )
                },
                onRestDayChange = { viewModel.onIntent(AddTodoIntent.OnRestDayChange(it)) },
                onSaveClick = { viewModel.onIntent(AddTodoIntent.OnSaveClick) },
            )

            AddTodoState.Page.NOTIFICATION -> NotificationScreen(
                state = state.notificationState,
                onBackClick = { viewModel.onIntent(AddTodoIntent.OnNotificationBackClick) },
                onSaveClick = { viewModel.onIntent(AddTodoIntent.OnNotificationSaveClick) },
                onNotificationToggleClick = { viewModel.onIntent(AddTodoIntent.OnNotificationToggleClick) },
                onAlarmTimeChange = { hour, minute ->
                    viewModel.onIntent(AddTodoIntent.OnAlarmTimeChange(hour, minute))
                },
                onMessageChange = { viewModel.onIntent(AddTodoIntent.OnAlarmMessageChange(it)) },
                onResetClick = { viewModel.onIntent(AddTodoIntent.OnAlarmMessageReset) },
            )
        }
    }
}

@Composable
private fun AddTodoScreen(
    state: AddTodoState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var isShowExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state.isModified) {
        isShowExitDialog = true
    }

    if (isShowExitDialog) {
        ConfirmExitDialog(
            onContinueClick = { isShowExitDialog = false },
            onExitClick = {
                isShowExitDialog = false
                onBackClick()
            },
        )
    }

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        AddTodoScreenPhone(
            state = state,
            onBackClick = {
                if (state.isModified) isShowExitDialog = true
                else onBackClick()
            },
            onSelectedDateChangeClick = onSelectedDateChangeClick,
            onTitleChange = onTitleChange,
            onPriorityChange = onPriorityChange,
            onTagDropDownClick = onTagDropDownClick,
            onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
            onRestDayChange = onRestDayChange,
            onSaveClick = onSaveClick,
            modifier = modifier,
        )
    } else {
        AddTodoScreenTablet(
            state = state,
            onBackClick = {
                if (state.isModified) isShowExitDialog = true
                else onBackClick()
            },
            onSelectedDateChangeClick = onSelectedDateChangeClick,
            onTitleChange = onTitleChange,
            onPriorityChange = onPriorityChange,
            onTagDropDownClick = onTagDropDownClick,
            onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
            onRestDayChange = onRestDayChange,
            onSaveClick = onSaveClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun AddTodoScreenPhone(
    state: AddTodoState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        EbbingSubTopBar(
            title = stringResource(R.string.home_add_todo_title),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 48.dp),
        ) {
            TodoMainFormContent(
                state = state,
                scrollState = scrollState,
                onSelectedDateChangeClick = onSelectedDateChangeClick,
                onTitleChange = onTitleChange,
                onTagDropDownClick = onTagDropDownClick,
                onPriorityChange = onPriorityChange,
                onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                onRestDayChange = onRestDayChange,
            )

            ScheduleContent(schedules = state.schedules)
        }

        EbbingSolidButton(
            label = stringResource(R.string.home_save),
            onClick = {
                onSaveClick()
                focusManager.clearFocus()
            },
            enabled = state.isSaveEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun AddTodoScreenTablet(
    state: AddTodoState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        EbbingSubTopBar(
            title = stringResource(R.string.home_add_todo_title),
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = stringResource(R.string.home_save),
                    style = if (state.isSaveEnabled) EbbingTheme.typography.body16M else EbbingTheme.typography.body16M,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.textDisabled,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(
                            throttleTime = 1500L,
                            enabled = state.isSaveEnabled
                        ) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 60.dp),
            ) {
                TodoMainFormContent(
                    state = state,
                    scrollState = scrollState,
                    onSelectedDateChangeClick = onSelectedDateChangeClick,
                    onTitleChange = onTitleChange,
                    onTagDropDownClick = onTagDropDownClick,
                    onPriorityChange = onPriorityChange,
                    onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                    onRestDayChange = onRestDayChange,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
                    .padding(horizontal = 20.dp),
            ) {
                ScheduleContent(schedules = state.schedules)
            }
        }
    }
}

@Composable
private fun TodoMainFormContent(
    state: AddTodoState,
    scrollState: ScrollState,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onPriorityChange: (String) -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
) {
    val monthDayText = stringResource(
        R.string.home_month_day,
        state.selectedDate.monthValue,
        state.selectedDate.dayOfMonth,
    )
    val addTodoHeaderSuffix = stringResource(R.string.home_add_todo_header_suffix)
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = EbbingTheme.colors.textPrimary,
                )
            ) {
                append(monthDayText)
            }
            append(addTodoHeaderSuffix)
        },
        style = EbbingTheme.typography.heading24B,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.clickable { onSelectedDateChangeClick() },
    )

    TitleContent(
        scrollState = scrollState,
        title = state.title,
        onTitleChange = onTitleChange,
    )

    TagContent(
        tag = state.tag,
        onTagDropDownClick = onTagDropDownClick,
    )

    PriorityContent(
        priority = state.priority,
        onPriorityChange = onPriorityChange,
    )

    RepeatCycleContent(
        repeatCycle = state.repeatCycle,
        onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
    )

    RestDayContent(
        restDays = state.restDays,
        onRestDayChange = onRestDayChange,
    )
}

@EbbingPreview
@Composable
private fun PreviewAddTodo() {
    BasePreview {
        AddTodoScreen(
            state = AddTodoState(
                selectedDate = LocalDate.now(),
                title = "토익",
                priority = "3",
                repeatCycle = RepeatCycleUiModel(
                    id = 1,
                    intervals = persistentListOf(1, 3, 7, 14, 30),
                    displayName = "1일, 3일, 7일, 14일, 30일"
                ),
                restDays = persistentSetOf(DayOfWeek.MONDAY),
            ),
            onSelectedDateChangeClick = {},
            onSaveClick = {},
            onBackClick = {},
            onTitleChange = {},
            onPriorityChange = {},
            onTagDropDownClick = {},
            onRepeatCycleDropDownClick = {},
            onRestDayChange = {},
        )
    }
}
