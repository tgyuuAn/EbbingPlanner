package com.tgyuu.shared.ui.feature.home.addtodo

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.util.throttledClickable
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.RepeatCycleBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.SelectedDateBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.TagBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.PriorityContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RepeatCycleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RestDayContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.ScheduleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TagContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TitleContent
import kotlinx.coroutines.launch

private enum class BottomSheetType {
    DATE, TAG, REPEAT_CYCLE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: AddTodoViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var showExitDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var currentBottomSheetType by remember { mutableStateOf<BottomSheetType?>(null) }

    // Bottom Sheet - content uses current state
    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            scope.launch {
                bottomSheetState.hide()
                currentBottomSheetType = null
            }
        },
        content = when (currentBottomSheetType) {
            BottomSheetType.DATE -> {
                {
                    SelectedDateBottomSheetContent(
                        originSelectedDate = state.selectedDate,
                        onDateSelected = { date ->
                            viewModel.onIntent(AddTodoIntent.OnSelectedDateChange(date))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                    )
                }
            }
            BottomSheetType.TAG -> {
                {
                    TagBottomSheetContent(
                        tagList = state.tagList,
                        selectedTag = state.tag,
                        onTagSelected = { tag ->
                            viewModel.onIntent(AddTodoIntent.OnTagChange(tag))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                        onAddTagClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                            viewModel.onIntent(AddTodoIntent.OnAddTagClick)
                        },
                    )
                }
            }
            BottomSheetType.REPEAT_CYCLE -> {
                {
                    RepeatCycleBottomSheetContent(
                        repeatCycleList = state.repeatCycleList,
                        selectedRepeatCycle = state.repeatCycle,
                        selectedDate = state.selectedDate,
                        onRepeatCycleSelected = { repeatCycle ->
                            viewModel.onIntent(AddTodoIntent.OnRepeatCycleChange(repeatCycle))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                        onAddRepeatCycleClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                            viewModel.onIntent(AddTodoIntent.OnAddRepeatCycleClick)
                        },
                    )
                }
            }
            null -> null
        },
    )

    if (showExitDialog) {
        ConfirmExitDialog(
            onContinueClick = { showExitDialog = false },
            onExitClick = {
                showExitDialog = false
                viewModel.onIntent(AddTodoIntent.OnBackClick)
            },
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 추가",
            onNavigationClick = {
                if (state.isModified) showExitDialog = true
                else viewModel.onIntent(AddTodoIntent.OnBackClick)
            },
            rightComponent = {
                if (!state.isTreatment) {
                    Text(
                        text = "저장",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                        else EbbingTheme.typography.bodyMM,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                        else EbbingTheme.colors.dark3,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .throttledClickable(
                                throttleTime = 1500L,
                                enabled = state.isSaveEnabled,
                            ) { viewModel.onIntent(AddTodoIntent.OnSaveClick) },
                    )
                }
            },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

            if (isWide) {
                // Tablet: two columns
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                    ) {
                        AddTodoFormContent(
                            state = state,
                            viewModel = viewModel,
                            onDateClick = {
                                currentBottomSheetType = BottomSheetType.DATE
                                scope.launch { bottomSheetState.show() }
                            },
                            onTagClick = {
                                currentBottomSheetType = BottomSheetType.TAG
                                scope.launch { bottomSheetState.show() }
                            },
                            onRepeatCycleClick = {
                                currentBottomSheetType = BottomSheetType.REPEAT_CYCLE
                                scope.launch { bottomSheetState.show() }
                            },
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                    ) {
                        ScheduleContent(schedules = state.schedules)
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            } else {
                // Phone: single column
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    AddTodoFormContent(
                        state = state,
                        viewModel = viewModel,
                        onDateClick = {
                            currentBottomSheetType = BottomSheetType.DATE
                            scope.launch { bottomSheetState.show() }
                        },
                        onTagClick = {
                            currentBottomSheetType = BottomSheetType.TAG
                            scope.launch { bottomSheetState.show() }
                        },
                        onRepeatCycleClick = {
                            currentBottomSheetType = BottomSheetType.REPEAT_CYCLE
                            scope.launch { bottomSheetState.show() }
                        },
                    )

                    ScheduleContent(schedules = state.schedules)

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = "저장",
                onClick = { viewModel.onIntent(AddTodoIntent.OnSaveClick) },
                enabled = state.isSaveEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun AddTodoFormContent(
    state: AddTodoState,
    viewModel: AddTodoViewModel,
    onDateClick: () -> Unit,
    onTagClick: () -> Unit,
    onRepeatCycleClick: () -> Unit,
) {
    // Header with date (clickable to change date)
    EbbingPartialUnderlineText(
        underlinedPart = "${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일",
        rest = " 부터\n시작하는 일정을 만들어요",
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
        highlightColor = EbbingTheme.colors.primaryDefault,
        modifier = Modifier.clickable(onClick = onDateClick),
    )

    TitleContent(
        title = state.title,
        onTitleChange = { viewModel.onIntent(AddTodoIntent.OnTitleChange(it)) },
    )

    TagContent(
        tag = state.tag,
        onTagDropDownClick = onTagClick,
    )

    PriorityContent(
        priority = state.priority,
        onPriorityChange = { viewModel.onIntent(AddTodoIntent.OnPriorityChange(it)) },
    )

    RepeatCycleContent(
        repeatCycle = state.repeatCycle,
        onRepeatCycleDropDownClick = onRepeatCycleClick,
    )

    RestDayContent(
        restDays = state.restDays,
        onRestDayChange = { viewModel.onIntent(AddTodoIntent.OnRestDayChange(it)) },
    )
}

@Composable
private fun ConfirmExitDialog(
    onContinueClick: () -> Unit,
    onExitClick: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onContinueClick) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = "작성 중인 일정이 사라져요!",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "지금 뒤로 가면 일정이 저장되지 않습니다.\n계속 이어서 작성해 보세요.",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "작성 중단하기",
                rightButtonText = "이어서 작성하기",
                onLeftButtonClick = onExitClick,
                onRightButtonClick = onContinueClick,
            )
        }
    }
}
