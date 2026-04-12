package com.tgyuu.shared.ui.feature.home.addtodo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
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
                Text(
                    text = "저장",
                    style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB
                    else EbbingTheme.typography.bodyMM,
                    color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault
                    else EbbingTheme.colors.dark3,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(enabled = state.isSaveEnabled) {
                            viewModel.onIntent(AddTodoIntent.OnSaveClick)
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
            // Header with date (clickable to change date)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일")
                    }
                    append(" 부터\n시작하는 일정을 만들어요")
                },
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.clickable {
                    currentBottomSheetType = BottomSheetType.DATE
                    scope.launch { bottomSheetState.show() }
                },
            )

            TitleContent(
                title = state.title,
                onTitleChange = { viewModel.onIntent(AddTodoIntent.OnTitleChange(it)) },
            )

            TagContent(
                tag = state.tag,
                onTagDropDownClick = {
                    currentBottomSheetType = BottomSheetType.TAG
                    scope.launch { bottomSheetState.show() }
                },
            )

            PriorityContent(
                priority = state.priority,
                onPriorityChange = { viewModel.onIntent(AddTodoIntent.OnPriorityChange(it)) },
            )

            RepeatCycleContent(
                repeatCycle = state.repeatCycle,
                onRepeatCycleDropDownClick = {
                    currentBottomSheetType = BottomSheetType.REPEAT_CYCLE
                    scope.launch { bottomSheetState.show() }
                },
            )

            RestDayContent(
                restDays = state.restDays,
                onRestDayChange = { viewModel.onIntent(AddTodoIntent.OnRestDayChange(it)) },
            )

            ScheduleContent(schedules = state.schedules)

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
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
                text = "작성 중인 내용이 있습니다",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = "정말 나가시겠습니까?",
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = "계속 작성",
                rightButtonText = "나가기",
                onLeftButtonClick = onContinueClick,
                onRightButtonClick = onExitClick,
            )
        }
    }
}
