package com.tgyuu.home.graph.ui.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.verticalScrollbar
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.model.RepeatCycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.until

@Composable
internal fun RepeatCycleBottomSheet(
    repeatCycleList: ImmutableList<RepeatCycleUiModel>,
    originRepeatCycle: RepeatCycleUiModel?,
    selectedDate: LocalDate,
    openKey: Int,
    startFromMonday: Boolean = false,
    updateRepeatCycle: (RepeatCycleUiModel) -> Unit,
    onAddRepeatCycleClick: () -> Unit,
) {
    var newRepeatCycle by remember(openKey) { mutableStateOf(originRepeatCycle) }
    var showEndDatePicker by remember(openKey) { mutableStateOf(false) }
    var dailyEndDate by remember(openKey) { mutableStateOf<LocalDate?>(null) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        val dayCountForHeader = dailyEndDate?.let {
            selectedDate.until(it, DateTimeUnit.DAY).toInt()
        }

        EbbingBottomSheetHeader(
            title = if (showEndDatePicker) stringResource(R.string.home_select_end_date) else stringResource(R.string.home_repeat_cycle),
            rightComponent = {
                if (showEndDatePicker) {
                    if (dayCountForHeader != null && dayCountForHeader > 0 && dayCountForHeader < RepeatCycle.MAX_DAILY_REPEAT_DAYS) {
                        Text(
                            text = stringResource(R.string.home_daily_repeat_total, dayCountForHeader + 1),
                            style = EbbingTheme.typography.body14M,
                            color = EbbingTheme.colors.primaryNormal,
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = EbbingTheme.colors.textOnBackground,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onAddRepeatCycleClick() },
                    )
                }
            })

        AnimatedVisibility(visible = !showEndDatePicker) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(top = 12.dp)
                    .verticalScrollbar(
                        state = listState,
                        color = EbbingTheme.colors.fillDisabled,
                    ),
            ) {
                items(
                    items = repeatCycleList,
                    key = { it.id },
                ) { cycle ->
                    EbbingBottomSheetListItemDefault(
                        label = cycle.displayName,
                        checked = cycle.id == newRepeatCycle?.id,
                        onChecked = {
                            newRepeatCycle = cycle
                            if (cycle.id == RepeatCycle.DAILY_REPEAT_ID) {
                                showEndDatePicker = true
                            }
                        },
                    )
                }
            }
        }

        AnimatedVisibility(visible = showEndDatePicker) {
            val calendarState = rememberCalendarState(selectedDate)

            Column {
                Text(
                    text = stringResource(
                        R.string.home_daily_repeat_prompt,
                        selectedDate.monthNumber,
                        selectedDate.dayOfMonth,
                    ),
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textDisabled,
                    modifier = Modifier.padding(top = 8.dp),
                )

                EbbingCalendar(
                    calendarState = calendarState,
                    schedulesByDateMap = emptyMap(),
                    showSyncButton = false,
                    startFromMonday = startFromMonday,
                    onSelectDate = { date ->
                        if (date > selectedDate) {
                            dailyEndDate = date
                        } else {
                            dailyEndDate = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                val dayCount = dailyEndDate?.let {
                    selectedDate.until(it, DateTimeUnit.DAY).toInt()
                }
                val exceedsMax = dayCount != null && dayCount >= RepeatCycle.MAX_DAILY_REPEAT_DAYS

                if (exceedsMax) {
                    Text(
                        text = stringResource(
                            R.string.home_daily_repeat_max,
                            RepeatCycle.MAX_DAILY_REPEAT_DAYS,
                        ),
                        style = EbbingTheme.typography.body14M,
                        color = EbbingTheme.colors.statusError,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        EbbingSolidButton(
            label = stringResource(R.string.home_apply),
            enabled = if (showEndDatePicker) {
                val dayCount = dailyEndDate?.let {
                    selectedDate.until(it, DateTimeUnit.DAY).toInt()
                }
                dayCount != null && dayCount > 0 && dayCount < RepeatCycle.MAX_DAILY_REPEAT_DAYS
            } else true,
            onClick = {
                if (showEndDatePicker) {
                    dailyEndDate?.let { endDate ->
                        val dayCount = selectedDate.until(endDate, DateTimeUnit.DAY).toInt()
                        val intervals = (0..dayCount).toList()
                        val endDateText = if (endDate.year != selectedDate.year) {
                            context.getString(
                                R.string.home_end_date_with_year,
                                endDate.year,
                                endDate.monthNumber,
                                endDate.dayOfMonth,
                            )
                        } else {
                            context.getString(
                                R.string.home_month_day,
                                endDate.monthNumber,
                                endDate.dayOfMonth,
                            )
                        }
                        val dailyCycle = RepeatCycleUiModel(
                            id = RepeatCycle.DAILY_REPEAT_ID,
                            intervals = intervals.toImmutableList(),
                            displayName = context.getString(
                                R.string.home_daily_repeat_display_name,
                                endDateText,
                            ),
                        )
                        updateRepeatCycle(dailyCycle)
                    }
                } else {
                    newRepeatCycle?.let { updateRepeatCycle(it) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
