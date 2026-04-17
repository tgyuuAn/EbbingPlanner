package com.tgyuu.shared.ui.feature.home.editdate

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.tgyuu.shared.designsystem.component.EbbingPartialUnderlineText
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toRelativeDayDescription
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.calendar.toKorean
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.feature.home.addtodo.component.RepeatCycleContent
import com.tgyuu.shared.ui.feature.home.addtodo.component.RestDayContent
import kotlinx.datetime.LocalDate

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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "일정 수정",
            onNavigationClick = { viewModel.onIntent(EditDateIntent.OnBackClick) },
            rightComponent = {
                if (!state.isTreatment) {
                    Text(
                        text = "저장",
                        style = EbbingTheme.typography.bodyMSB,
                        color = EbbingTheme.colors.primaryDefault,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                viewModel.onIntent(EditDateIntent.OnSaveClick(isDoneSchedules.toList()))
                            },
                    )
                }
            },
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
                    EbbingPartialUnderlineText(
                        underlinedPart = "${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일",
                        rest = " 부터\n시작하는 일정으로 바꿔요",
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                        modifier = Modifier.clickable {
                            viewModel.onIntent(EditDateIntent.OnSelectedDateDropDownClick)
                        },
                    )
                    RepeatCycleContent(
                        repeatCycle = state.repeatCycle,
                        onRepeatCycleDropDownClick = {
                            viewModel.onIntent(EditDateIntent.OnRepeatCycleDropDownClick)
                        },
                    )
                    RestDayContent(
                        restDays = state.restDays,
                        onRestDayChange = { viewModel.onIntent(EditDateIntent.OnRestDayChange(it)) },
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp),
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
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .imePadding(),
            ) {
                EbbingPartialUnderlineText(
                    underlinedPart = "${state.selectedDate.monthNumber}월 ${state.selectedDate.dayOfMonth}일",
                    rest = " 부터\n시작하는 일정으로 바꿔요",
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(EditDateIntent.OnSelectedDateDropDownClick)
                    },
                )
                RepeatCycleContent(
                    repeatCycle = state.repeatCycle,
                    onRepeatCycleDropDownClick = {
                        viewModel.onIntent(EditDateIntent.OnRepeatCycleDropDownClick)
                    },
                )
                RestDayContent(
                    restDays = state.restDays,
                    onRestDayChange = { viewModel.onIntent(EditDateIntent.OnRestDayChange(it)) },
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

        if (state.isTreatment) {
            com.tgyuu.shared.designsystem.component.EbbingSolidButton(
                label = "저장",
                onClick = { viewModel.onIntent(EditDateIntent.OnSaveClick(isDoneSchedules.toList())) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.background)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            )
        }
    }
    } // BoxWithConstraints
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

    Column(modifier = modifier) {
        Text(
            text = "${schedules.size} 개의 학습 일정",
            style = EbbingTheme.typography.headingMSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(top = 32.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EbbingTheme.colors.light3)
        ) {
            schedules.forEachIndexed { idx, item ->
                ScheduleCheckCard(
                    idx = idx + 1,
                    isChecked = isDoneSchedules.getOrElse(idx) { false },
                    colorValue = colorValue,
                    schedule = item,
                    onCheckSchedule = { onCheckSchedule(idx) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 16.dp,
                        )
                )
            }
        }
    }
}

@Composable
private fun ScheduleCheckCard(
    idx: Int,
    isChecked: Boolean,
    colorValue: Int,
    schedule: LocalDate,
    onCheckSchedule: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        EbbingCheck(
            checked = isChecked,
            colorValue = colorValue,
            onCheckedChange = { onCheckSchedule() },
        )

        Text(
            text = idx.toString(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = schedule.toRelativeDayDescription(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )
    }
}

@Composable
private fun DescriptionBody(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "- 일정이 변경되면 기존 일정의 완료 여부는 초기화 됩니다.\n" +
                "- 위 체크 박스에서 새로운 일정에 완료 여부를 설정할 수 있습니다.\n" +
                "- 일정을 변경하게 되면 기존 일정에 있던 메모들이 제거됩니다.",
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark3,
        modifier = modifier,
    )
}
