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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.verticalScrollbar
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = if (showEndDatePicker) "종료일 선택" else "반복 주기",
            rightComponent = {
                if (!showEndDatePicker) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = EbbingTheme.colors.black,
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
                        color = EbbingTheme.colors.light1,
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
                    text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일부터 언제까지 반복할까요?",
                    style = EbbingTheme.typography.bodyMM,
                    color = EbbingTheme.colors.dark2,
                    modifier = Modifier.padding(top = 8.dp),
                )

                EbbingCalendar(
                    calendarState = calendarState,
                    schedulesByDateMap = emptyMap(),
                    showSyncButton = false,
                    startFromMonday = startFromMonday,
                    onSelectDate = { date ->
                        if (!date.isBefore(selectedDate)) {
                            dailyEndDate = date
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                val dayCount = dailyEndDate?.let {
                    ChronoUnit.DAYS.between(selectedDate, it).toInt()
                }
                val exceedsMax = dayCount != null && dayCount > RepeatCycle.MAX_DAILY_REPEAT_DAYS

                if (exceedsMax) {
                    Text(
                        text = "최대 ${RepeatCycle.MAX_DAILY_REPEAT_DAYS}일까지 설정할 수 있습니다",
                        style = EbbingTheme.typography.bodySM,
                        color = EbbingTheme.colors.error,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                } else if (dayCount != null) {
                    Text(
                        text = "총 ${dayCount + 1}일간 매일 반복",
                        style = EbbingTheme.typography.bodySM,
                        color = EbbingTheme.colors.primaryDefault,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        EbbingSolidButton(
            label = if (showEndDatePicker) "적용하기" else "적용하기",
            enabled = if (showEndDatePicker) {
                val dayCount = dailyEndDate?.let {
                    ChronoUnit.DAYS.between(selectedDate, it).toInt()
                }
                dayCount != null && dayCount >= 0 && dayCount <= RepeatCycle.MAX_DAILY_REPEAT_DAYS
            } else true,
            onClick = {
                if (showEndDatePicker) {
                    dailyEndDate?.let { endDate ->
                        val dayCount = ChronoUnit.DAYS.between(selectedDate, endDate).toInt()
                        val intervals = (0..dayCount).toList()
                        val endDateText = if (endDate.year != selectedDate.year) {
                            "${endDate.year}년 ${endDate.monthValue}월 ${endDate.dayOfMonth}일"
                        } else {
                            "${endDate.monthValue}월 ${endDate.dayOfMonth}일"
                        }
                        val dailyCycle = RepeatCycleUiModel(
                            id = RepeatCycle.DAILY_REPEAT_ID,
                            intervals = intervals.toImmutableList(),
                            displayName = "매일하기 ($endDateText 까지)",
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
