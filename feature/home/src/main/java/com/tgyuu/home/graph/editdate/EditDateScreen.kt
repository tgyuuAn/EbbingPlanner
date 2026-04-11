package com.tgyuu.home.graph.editdate

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
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
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.home.graph.ui.bottomsheet.RepeatCycleBottomSheet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import com.tgyuu.home.graph.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.editdate.contract.EditDateIntent
import com.tgyuu.home.graph.editdate.contract.EditDateState
import com.tgyuu.home.graph.ui.RepeatCycleContent
import com.tgyuu.home.graph.ui.RestDayContent
import com.tgyuu.home.graph.ui.ScheduleCheckContent
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
internal fun EditDateRoute(
    viewModel: EditDateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var repeatCycleSheetKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(viewModel) {
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
            repeatCycleSheetKey++
            viewModel.onIntent(
                EditDateIntent.OnRepeatCycleDropDownClick(
                    {
                        RepeatCycleBottomSheet(
                            repeatCycleList = state.repeatCycleList,
                            originRepeatCycle = state.repeatCycle,
                            selectedDate = state.selectedDate,
                            openKey = repeatCycleSheetKey,
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
        onSaveClick = { viewModel.onIntent(EditDateIntent.OnSaveClick(it)) },
    )
}

@Composable
private fun EditDateScreen(
    state: EditDateState,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
    onSaveClick: (List<Boolean>) -> Unit,
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
    onSaveClick: (List<Boolean>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isDoneSchedules = remember(state.schedules.size) {
        mutableStateListOf(*List(state.schedules.size) { false }.toTypedArray())
    }

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
                            onSaveClick(isDoneSchedules)
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
                onSelectedDateChangeClick = onSelectedDateChangeClick,
                onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                onRestDayChange = onRestDayChange,
            )

            ScheduleCheckContent(
                schedules = state.schedules,
                isDoneSchedules = isDoneSchedules,
                colorValue = state.originTagColor,
                onCheckSchedule = { idx -> isDoneSchedules[idx] = !isDoneSchedules[idx] },
            )

            HorizontalDivider(
                color = EbbingTheme.colors.light2,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            DescriptionBody()

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
    onSaveClick: (List<Boolean>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isDoneSchedules = remember(state.schedules.size) {
        mutableStateListOf(*List(state.schedules.size) { false }.toTypedArray())
    }

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
                            onSaveClick(isDoneSchedules)
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
                ScheduleCheckContent(
                    schedules = state.schedules,
                    isDoneSchedules = isDoneSchedules,
                    colorValue = state.originTagColor,
                    onCheckSchedule = { idx -> isDoneSchedules[idx] = !isDoneSchedules[idx] },
                )

                HorizontalDivider(
                    color = EbbingTheme.colors.light2,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                DescriptionBody()
            }
        }
    }
}

@Composable
private fun EditDateMainFormContent(
    state: EditDateState,
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

@Composable
private fun DescriptionBody() {
    Text(
        text = "- 일정이 변경되면 기존 일정의 완료 여부는 초기화 됩니다.\n" +
                "- 위 체크 박스에서 새로운 일정에 완료 여부를 설정할 수 있습니다.\n" +
                "- 일정을 변경하게 되면 기존 일정에 있던 메모들이 제거됩니다.",
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark3,
    )
}


@EbbingPreview
@Composable
private fun PreviewEditDate() {
    BasePreview {
        EditDateScreen(
            state = EditDateState(
                selectedDate = LocalDate.now(),
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
            onRepeatCycleDropDownClick = {},
            onRestDayChange = {},
        )
    }
}
