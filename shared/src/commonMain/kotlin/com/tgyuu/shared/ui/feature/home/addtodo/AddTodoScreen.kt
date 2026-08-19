package com.tgyuu.shared.ui.feature.home.addtodo

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalFocusManager
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import androidx.compose.ui.text.style.TextAlign
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
import com.tgyuu.shared.ui.feature.home.addtodo.component.PinnedContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RepeatCycleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RestDayContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.ScheduleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TagContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.TitleContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_notice
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_add_todo_button
import ebbingplanner.shared.generated.resources.home_add_todo_header_suffix
import ebbingplanner.shared.generated.resources.home_add_todo_title
import ebbingplanner.shared.generated.resources.home_exit_confirm_continue
import ebbingplanner.shared.generated.resources.home_exit_confirm_stop
import ebbingplanner.shared.generated.resources.home_exit_confirm_sub
import ebbingplanner.shared.generated.resources.home_exit_confirm_title
import ebbingplanner.shared.generated.resources.home_month_day
import org.jetbrains.compose.resources.stringResource

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
    val focusManager = LocalFocusManager.current
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

    // 저장 버튼까지 포함하는 최상위 Column에 imePadding을 적용해 키보드에 버튼이 가리지 않게 함 (Android 동일)
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        EbbingSubTopBar(
            title = stringResource(Res.string.home_add_todo_title),
            onNavigationClick = {
                if (state.isModified) showExitDialog = true
                else viewModel.onIntent(AddTodoIntent.OnBackClick)
            },
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

            if (isWide) {
                // Tablet: two columns
                Row(
                    modifier = Modifier.fillMaxSize(),
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
                        androidx.compose.animation.AnimatedVisibility(visible = state.schedules.isNotEmpty()) {
                        ScheduleContent(schedules = state.schedules)
                    }
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            } else {
                // Phone: single column
                Column(
                    modifier = Modifier
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

                    androidx.compose.animation.AnimatedVisibility(visible = state.schedules.isNotEmpty()) {
                        ScheduleContent(schedules = state.schedules)
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }

        com.tgyuu.shared.designsystem.component.EbbingSolidButton(
            label = stringResource(Res.string.home_add_todo_button),
            onClick = {
                focusManager.clearFocus()
                viewModel.onIntent(AddTodoIntent.OnSaveClick)
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
private fun AddTodoFormContent(
    state: AddTodoState,
    viewModel: AddTodoViewModel,
    onDateClick: () -> Unit,
    onTagClick: () -> Unit,
    onRepeatCycleClick: () -> Unit,
) {
    // Header with date (clickable to change date)
    EbbingPartialUnderlineText(
        underlinedPart = stringResource(Res.string.home_month_day, state.selectedDate.monthNumber, state.selectedDate.dayOfMonth),
        rest = stringResource(Res.string.home_add_todo_header_suffix),
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

    RepeatCycleContent(
        repeatCycle = state.repeatCycle,
        onRepeatCycleDropDownClick = onRepeatCycleClick,
    )

    RestDayContent(
        restDays = state.restDays,
        onRestDayChange = { viewModel.onIntent(AddTodoIntent.OnRestDayChange(it)) },
    )

    PinnedContent(
        isPinned = state.isPinned,
        onPinnedChange = { viewModel.onIntent(AddTodoIntent.OnPinnedChange(it)) },
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
            // Android EbbingDialogIconTop과 동일: 상단 notice 아이콘
            Image(
                painter = painterResource(Res.drawable.ic_notice),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 40.dp)
                    .size(40.dp),
            )
            Text(
                text = stringResource(Res.string.home_exit_confirm_title),
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = stringResource(Res.string.home_exit_confirm_sub),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.home_exit_confirm_stop),
                rightButtonText = stringResource(Res.string.home_exit_confirm_continue),
                onLeftButtonClick = onExitClick,
                onRightButtonClick = onContinueClick,
            )
        }
    }
}
