package com.tgyuu.home.graph.editdate

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
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.RepeatCycleBottomSheet
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.editdate.contract.EditDateIntent
import com.tgyuu.home.graph.editdate.contract.EditDateState
import com.tgyuu.home.graph.ui.RepeatCycleContent
import com.tgyuu.home.graph.ui.RestDayContent
import com.tgyuu.home.graph.ui.ScheduleContent
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
internal fun EditDateRoute(
    viewModel: EditDateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadNewRepeatCycle()
        viewModel.loadRepeatCycles()
    }

    EditDateScreen(
        state = state,
        onBackClick = { viewModel.onIntent(EditDateIntent.OnBackClick) },
        onSelectedDateChangeClick = {
            viewModel.onIntent(
                EditDateIntent.OnSelectedDataChangeClick(
                    {
                        SelectedDateBottomSheet(
                            originSelectedDate = state.selectedDate,
                            schedulesByDateMap = emptyMap(),
                            updateSelectedDate = {
                                viewModel.onIntent(EditDateIntent.OnSelectedDateChange(it))
                            },
                        )
                    }
                )
            )
        },
        onRepeatCycleDropDownClick = {
            viewModel.onIntent(
                EditDateIntent.OnRepeatCycleDropDownClick(
                    {
                        RepeatCycleBottomSheet(
                            repeatCycleList = state.repeatCycleList,
                            originRepeatCycle = state.repeatCycle,
                            onAddRepeatCycleClick = {
                                viewModel.onIntent(EditDateIntent.OnAddRepeatCycleClick)
                            },
                            updateRepeatCycle = {
                                viewModel.onIntent(
                                    EditDateIntent.OnRepeatCycleChange(it)
                                )
                            },
                        )
                    }
                )
            )
        },
        onRestDayChange = { viewModel.onIntent(EditDateIntent.OnRestDayChange(it)) },
        onSaveClick = { viewModel.onIntent(EditDateIntent.OnSaveClick) },
    )
}

@Composable
private fun EditDateScreen(
    state: EditDateState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        EditDateScreenPhone(
            state = state,
            onBackClick = onBackClick,
            onSelectedDateChangeClick = onSelectedDateChangeClick,
            onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
            onRestDayChange = onRestDayChange,
            onSaveClick = onSaveClick,
            modifier = modifier,
        )
    } else {
        EditDateScreenTablet(
            state = state,
            onBackClick = onBackClick,
            onSelectedDateChangeClick = onSelectedDateChangeClick,
            onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
            onRestDayChange = onRestDayChange,
            onSaveClick = onSaveClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun EditDateScreenPhone(
    state: EditDateState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 수정",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.primaryDefault,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L) {
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
            EditDateMainFormContent(
                state = state,
                scrollState = scrollState,
                onSelectedDateChangeClick = onSelectedDateChangeClick,
                onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                onRestDayChange = onRestDayChange,
            )

            ScheduleContent(schedules = state.schedules)
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun EditDateScreenTablet(
    state: EditDateState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 수정",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.primaryDefault,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .throttledClickable(throttleTime = 1500L) {
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
                EditDateMainFormContent(
                    state = state,
                    scrollState = scrollState,
                    onSelectedDateChangeClick = onSelectedDateChangeClick,
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
private fun EditDateMainFormContent(
    state: EditDateState,
    scrollState: ScrollState,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
            }
            append(" 부터\n시작하는 일정으로 바꿔요")
        },
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.clickable { onSelectedDateChangeClick() },
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
private fun PreviewEditDate() {
    BasePreview {
        EditDateScreen(
            state = EditDateState(
                selectedDate = LocalDate.now(),
                repeatCycle = DefaultRepeatCycles.last(),
                restDays = setOf(DayOfWeek.MONDAY),
            ),
            onSelectedDateChangeClick = {},
            onSaveClick = {},
            onBackClick = {},
            onRepeatCycleDropDownClick = {},
            onRestDayChange = {},
        )
    }
}
