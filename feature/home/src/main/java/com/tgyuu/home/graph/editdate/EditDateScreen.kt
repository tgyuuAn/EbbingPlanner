package com.tgyuu.home.graph.editdate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.designsystem.R
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
                            startFromMonday = state.mondayStart,
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
                            startFromMonday = state.mondayStart,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        EbbingSubTopBar(
            title = stringResource(R.string.home_edit_todo_title),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp),
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
                color = EbbingTheme.colors.fillTextfield,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            DescriptionBody()

            Spacer(modifier = Modifier.height(60.dp))
        }

        EbbingSolidButton(
            label = stringResource(R.string.home_edit_todo_button),
            onClick = {
                onSaveClick(isDoneSchedules)
                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
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
            title = stringResource(R.string.home_edit_todo_title),
            onNavigationClick = onBackClick,
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
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
                    color = EbbingTheme.colors.fillTextfield,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                DescriptionBody()
            }
        }

        EbbingSolidButton(
            label = stringResource(R.string.home_edit_todo_button),
            onClick = {
                onSaveClick(isDoneSchedules)
                focusManager.clearFocus()
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun EditDateMainFormContent(
    state: EditDateState,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
) {
    val monthDayText = stringResource(
        R.string.home_month_day,
        state.selectedDate.monthValue,
        state.selectedDate.dayOfMonth,
    )
    val editDateHeaderSuffix = stringResource(R.string.home_edit_date_header_suffix)
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(monthDayText)
            }
            append(editDateHeaderSuffix)
        },
        style = EbbingTheme.typography.heading24B,
        color = EbbingTheme.colors.textOnBackground,
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
        text = stringResource(R.string.home_edit_date_description),
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textDisabled,
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
