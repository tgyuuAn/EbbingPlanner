package com.tgyuu.shared.ui.feature.home.editdate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toRelativeDayLabel
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.calendar.toLocalizedShort
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.RepeatCycleBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet.SelectedDateBottomSheetContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.PinnedContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RepeatCycleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RestDayContent
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_edit_date_description
import ebbingplanner.shared.generated.resources.home_edit_date_header_suffix
import ebbingplanner.shared.generated.resources.home_edit_todo_button
import ebbingplanner.shared.generated.resources.home_edit_todo_title
import ebbingplanner.shared.generated.resources.home_month_day
import ebbingplanner.shared.generated.resources.home_schedule_date_day
import ebbingplanner.shared.generated.resources.home_study_schedule_count
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

private enum class EditDateBottomSheetType {
    DATE, REPEAT_CYCLE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDateScreen(
    viewModel: EditDateViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val isDoneSchedules = remember(state.schedules.size) {
        mutableStateListOf(*List(state.schedules.size) { false }.toTypedArray())
    }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()
    var currentBottomSheetType by remember { mutableStateOf<EditDateBottomSheetType?>(null) }

    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            scope.launch {
                bottomSheetState.hide()
                currentBottomSheetType = null
            }
        },
        content = when (currentBottomSheetType) {
            EditDateBottomSheetType.DATE -> {
                {
                    SelectedDateBottomSheetContent(
                        originSelectedDate = state.selectedDate,
                        startFromMonday = state.mondayStart,
                        onDateSelected = { date ->
                            viewModel.onIntent(EditDateIntent.OnSelectedDateChange(date))
                            scope.launch {
                                bottomSheetState.hide()
                                currentBottomSheetType = null
                            }
                        },
                    )
                }
            }
            EditDateBottomSheetType.REPEAT_CYCLE -> {
                {
                    RepeatCycleBottomSheetContent(
                        repeatCycleList = state.repeatCycleList,
                        selectedRepeatCycle = state.repeatCycle,
                        selectedDate = state.selectedDate,
                        startFromMonday = state.mondayStart,
                        onRepeatCycleSelected = { repeatCycle ->
                            viewModel.onIntent(EditDateIntent.OnRepeatCycleChange(repeatCycle))
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
                            viewModel.onIntent(EditDateIntent.OnAddRepeatCycleClick)
                        },
                    )
                }
            }
            null -> null
        },
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT

        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.home_edit_todo_title),
                onNavigationClick = { viewModel.onIntent(EditDateIntent.OnBackClick) },
                rightComponent = {},
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            if (isWide) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .imePadding(),
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(20.dp),
                    ) {
                        EditDateMainFormContent(
                            state = state,
                            onSelectedDateChangeClick = {
                                currentBottomSheetType = EditDateBottomSheetType.DATE
                                scope.launch { bottomSheetState.show() }
                            },
                            onRepeatCycleDropDownClick = {
                                currentBottomSheetType = EditDateBottomSheetType.REPEAT_CYCLE
                                scope.launch { bottomSheetState.show() }
                            },
                            onRestDayChange = { viewModel.onIntent(EditDateIntent.OnRestDayChange(it)) },
                            onPinnedChange = { viewModel.onIntent(EditDateIntent.OnPinnedChange(it)) },
                        )
                        Spacer(modifier = Modifier.height(60.dp))
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                    ) {
                        ScheduleCheckContent(
                            schedules = state.schedules,
                            isDoneSchedules = isDoneSchedules,
                            colorValue = state.originTagColor,
                            onCheckSchedule = { idx -> isDoneSchedules[idx] = !isDoneSchedules[idx] },
                        )

                        DescriptionBody()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                        .imePadding(),
                ) {
                    EditDateMainFormContent(
                        state = state,
                        onSelectedDateChangeClick = {
                            currentBottomSheetType = EditDateBottomSheetType.DATE
                            scope.launch { bottomSheetState.show() }
                        },
                        onRepeatCycleDropDownClick = {
                            currentBottomSheetType = EditDateBottomSheetType.REPEAT_CYCLE
                            scope.launch { bottomSheetState.show() }
                        },
                        onRestDayChange = { viewModel.onIntent(EditDateIntent.OnRestDayChange(it)) },
                        onPinnedChange = { viewModel.onIntent(EditDateIntent.OnPinnedChange(it)) },
                    )

                    ScheduleCheckContent(
                        schedules = state.schedules,
                        isDoneSchedules = isDoneSchedules,
                        colorValue = state.originTagColor,
                        onCheckSchedule = { idx -> isDoneSchedules[idx] = !isDoneSchedules[idx] },
                    )

                    DescriptionBody()

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            EbbingSolidButton(
                label = stringResource(Res.string.home_edit_todo_button),
                onClick = { viewModel.onIntent(EditDateIntent.OnSaveClick(isDoneSchedules.toList())) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun EditDateMainFormContent(
    state: EditDateState,
    onSelectedDateChangeClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (kotlinx.datetime.DayOfWeek) -> Unit,
    onPinnedChange: (Boolean) -> Unit,
) {
    EbbingPartialUnderlineText(
        underlinedPart = stringResource(
            Res.string.home_month_day,
            state.selectedDate.monthNumber,
            state.selectedDate.dayOfMonth,
        ),
        rest = stringResource(Res.string.home_edit_date_header_suffix),
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
        highlightColor = EbbingTheme.colors.primaryDefault,
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

    PinnedContent(
        isPinned = state.isPinned,
        onPinnedChange = onPinnedChange,
    )
}

@Composable
private fun ScheduleCheckContent(
    schedules: List<LocalDate>,
    isDoneSchedules: List<Boolean>,
    colorValue: Int,
    onCheckSchedule: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (schedules.isEmpty()) return

    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .clip(shape)
            .border(width = 1.dp, color = EbbingTheme.colors.light2, shape = shape)
            .background(EbbingTheme.colors.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_study_schedule_count, schedules.size),
            style = EbbingTheme.typography.headingMB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        schedules.forEachIndexed { idx, item ->
            ScheduleCheckCard(
                idx = idx + 1,
                isChecked = isDoneSchedules.getOrElse(idx) { false },
                colorValue = colorValue,
                schedule = item,
                showDivider = idx < schedules.lastIndex,
                onCheckSchedule = { onCheckSchedule(idx) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ScheduleCheckCard(
    idx: Int,
    colorValue: Int,
    isChecked: Boolean,
    schedule: LocalDate,
    showDivider: Boolean,
    onCheckSchedule: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(EbbingTheme.colors.light3),
                ) {
                    Text(
                        text = idx.toString(),
                        style = EbbingTheme.typography.captionR12,
                        color = EbbingTheme.colors.dark3,
                    )
                }

                Text(
                    text = stringResource(
                        Res.string.home_schedule_date_day,
                        schedule.toFormattedString(),
                        schedule.dayOfWeek.toLocalizedShort(),
                    ),
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.black,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = schedule.toRelativeDayLabel(),
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark3,
                )

                EbbingCheck(
                    checked = isChecked,
                    colorValue = colorValue,
                    onCheckedChange = { onCheckSchedule() },
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(EbbingTheme.colors.light2),
            )
        }
    }
}

@Composable
private fun DescriptionBody(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.home_edit_date_description),
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark3,
        modifier = modifier.padding(top = 24.dp),
    )
}
