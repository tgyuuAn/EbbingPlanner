package com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetListItemDefault
import com.tgyuu.shared.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.shared.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.until

@Composable
fun RepeatCycleBottomSheetContent(
    repeatCycleList: ImmutableList<RepeatCycleUiModel>,
    selectedRepeatCycle: RepeatCycleUiModel?,
    selectedDate: LocalDate,
    openKey: Int = 0,
    startFromMonday: Boolean = false,
    onRepeatCycleSelected: (RepeatCycleUiModel) -> Unit,
    onAddRepeatCycleClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var newRepeatCycle by remember(openKey) { mutableStateOf(selectedRepeatCycle) }
    var showEndDatePicker by remember(openKey) { mutableStateOf(false) }
    var dailyEndDate by remember(openKey) { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        val dayCountForHeader = dailyEndDate?.let {
            selectedDate.until(it, DateTimeUnit.DAY).toInt()
        }

        EbbingBottomSheetHeader(
            title = if (showEndDatePicker) "종료일 선택" else "반복 주기",
            rightComponent = {
                if (showEndDatePicker) {
                    if (dayCountForHeader != null && dayCountForHeader > 0 && dayCountForHeader < RepeatCycle.MAX_DAILY_REPEAT_DAYS) {
                        Text(
                            text = "총 ${dayCountForHeader + 1}일간 매일 반복",
                            style = EbbingTheme.typography.bodySM,
                            color = EbbingTheme.colors.primaryDefault,
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "반복 주기 추가",
                        tint = EbbingTheme.colors.black,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onAddRepeatCycleClick() },
                    )
                }
            }
        )

        AnimatedVisibility(visible = !showEndDatePicker) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(top = 12.dp),
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
                    text = "${selectedDate.monthNumber}월 ${selectedDate.dayOfMonth}일부터 언제까지 반복할까요?",
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.light1,
                    modifier = Modifier.padding(top = 8.dp),
                )

                EbbingCalendar(
                    calendarState = calendarState,
                    schedulesByDateMap = emptyMap(),
                    startFromMonday = startFromMonday,
                    onSelectDate = { date ->
                        dailyEndDate = if (date > selectedDate) date else null
                    },
                    showSyncButton = false,
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
                        text = "최대 ${RepeatCycle.MAX_DAILY_REPEAT_DAYS}일까지 설정할 수 있습니다",
                        style = EbbingTheme.typography.bodySM,
                        color = EbbingTheme.colors.error,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        EbbingSolidButton(
            label = "적용하기",
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
                            "${endDate.year}년 ${endDate.monthNumber}월 ${endDate.dayOfMonth}일"
                        } else {
                            "${endDate.monthNumber}월 ${endDate.dayOfMonth}일"
                        }
                        val dailyCycle = RepeatCycleUiModel(
                            id = RepeatCycle.DAILY_REPEAT_ID,
                            intervals = intervals.toImmutableList(),
                            displayName = "매일하기 ($endDateText 까지)",
                        )
                        onRepeatCycleSelected(dailyCycle)
                    }
                } else {
                    newRepeatCycle?.let { onRepeatCycleSelected(it) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
