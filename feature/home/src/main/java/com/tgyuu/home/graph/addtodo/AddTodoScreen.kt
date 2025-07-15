package com.tgyuu.home.graph.addtodo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.RepeatCycleBottomSheet
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.TagBottomSheet
import com.tgyuu.home.graph.addtodo.ui.dialog.ConfirmExitDialog
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

    LaunchedEffect(viewModel) {
        viewModel.loadNewTag()
        viewModel.loadNewRepeatCycle()
        viewModel.loadTags()
        viewModel.loadRepeatCycles()
    }

    AddTodoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(AddTodoIntent.OnBackClick) },
        onSelectedDateChangeClick = {
            viewModel.onIntent(
                AddTodoIntent.OnSelectedDataChangeClick(
                    {
                        SelectedDateBottomSheet(
                            originSelectedDate = state.selectedDate,
                            schedulesByDateMap = emptyMap(),
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
            viewModel.onIntent(
                AddTodoIntent.OnRepeatCycleDropDownClick(
                    {
                        RepeatCycleBottomSheet(
                            repeatCycleList = state.repeatCycleList,
                            originRepeatCycle = state.repeatCycle,
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

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 추가",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L, enabled = state.isSaveEnabled) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp)
                .imePadding(),
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

            Spacer(modifier = Modifier.height(60.dp))
        }
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

    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 추가",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L, enabled = state.isSaveEnabled) {
                            onSaveClick()
                            focusManager.clearFocus()
                        },
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .padding(horizontal = 20.dp),
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

                Spacer(modifier = Modifier.height(60.dp))
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
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
            }
            append(" 부터\n시작하는 일정을 만들어요")
        },
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
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
                repeatCycle = DefaultRepeatCycles.last(),
                restDays = setOf(DayOfWeek.MONDAY),
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
