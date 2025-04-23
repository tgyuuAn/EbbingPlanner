package com.tgyuu.home.graph.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.common.daysBetween
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.animateScrollWhenFocus
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingChip
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.EbbingTextInputDropDown
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.home.graph.add.ui.InputState
import com.tgyuu.home.graph.main.model.TodoRO
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
internal fun AddTodoRoute(
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddTodoScreen(
        selectedDate = state.selectedDate,
        isSaveButtonEnabled = true,
        onBackClick = {},
        onSelectedDateChangeClick = {},
        onSaveClick = {},
    )
}

@Composable
private fun AddTodoScreen(
    selectedDate: LocalDate,
    isSaveButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        EbbingSubTopBar(
            title = "일정 추가",
            onNavigationClick = onBackClick,
            rightComponent = {
                Text(
                    text = "저장",
                    style = EbbingTheme.typography.bodyMM,
                    color = if (isSaveButtonEnabled) {
                        EbbingTheme.colors.primaryDefault
                    } else {
                        EbbingTheme.colors.dark3
                    },
                    modifier = Modifier.throttledClickable(
                        throttleTime = 1500L,
                        enabled = isSaveButtonEnabled
                    ) {
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
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일")
                    }
                    append(" 부터\n시작하는 일정을 만듭니다.")
                },
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.clickable { onSelectedDateChangeClick() },
            )

            TitleContent(
                scrollState = scrollState,
                title = "",
                titleInputState = InputState.DEFAULT,
                onTitleChanged = {},
            )

            TagContent(
                tag = "",
                tagInputState = InputState.DEFAULT,
                onTagDropDownClicked = {},
            )

            RepeatCycleContent(
                repeatCycle = "",
                repeatCycleInputState = InputState.DEFAULT,
                onRepeatCycleDropDownClicked = {},
            )

            RestDayContent(
                restDays = setOf(DayOfWeek.MONDAY),
                onRestDayChanged = {},
            )

            ScheduleContent(
                selectedDate = selectedDate,
                schedules = listOf(
                    TodoRO(
                        id = 1,
                        infoId = 100,
                        title = "프로젝트 기획서 작성",
                        description = "새 서비스 런칭을 위한 기획서를 작성하고 팀에 공유합니다.",
                        tagId = 10,
                        name = "기획",
                        color = 0xFFBB86FC.toInt(),
                        date = LocalDate.of(2025, 4, 25),
                        memo = "팀 회의 전까지 초안 완성",
                        priority = 2,
                        isDone = false,
                        createdAt = LocalDate.of(2025, 4, 20),
                        updatedAt = LocalDate.of(2025, 4, 20)
                    ),
                    TodoRO(
                        id = 2,
                        infoId = 101,
                        title = "UI 디자인 검토",
                        description = "디자이너가 보낸 화면 설계 시안을 리뷰하고 피드백을 남깁니다.",
                        tagId = 11,
                        name = "디자인",
                        color = 0xFF03DAC5.toInt(),
                        date = LocalDate.of(2025, 4, 26),
                        memo = "상세 컴포넌트별 코멘트 필요",
                        priority = 3,
                        isDone = false,
                        createdAt = LocalDate.of(2025, 4, 21),
                        updatedAt = LocalDate.of(2025, 4, 21)
                    ),
                    TodoRO(
                        id = 3,
                        infoId = 102,
                        title = "API 문서 작성",
                        description = "백엔드 팀과 협업하여 새 API 명세서를 완성합니다.",
                        tagId = 12,
                        name = "백엔드",
                        color = 0xFFFF5722.toInt(),
                        date = LocalDate.of(2025, 4, 27),
                        memo = "예외 케이스까지 상세 기재",
                        priority = 1,
                        isDone = false,
                        createdAt = LocalDate.of(2025, 4, 22),
                        updatedAt = LocalDate.of(2025, 4, 22)
                    ),
                    TodoRO(
                        id = 4,
                        infoId = 103,
                        title = "유닛 테스트 작성",
                        description = "Compose 화면 로직과 ViewModel에 대한 유닛 테스트를 작성합니다.",
                        tagId = 13,
                        name = "테스트",
                        color = 0xFF4CAF50.toInt(),
                        date = LocalDate.of(2025, 4, 28),
                        memo = "테스트 커버리지 80% 목표",
                        priority = 2,
                        isDone = false,
                        createdAt = LocalDate.of(2025, 4, 23),
                        updatedAt = LocalDate.of(2025, 4, 23)
                    ),
                    TodoRO(
                        id = 5,
                        infoId = 104,
                        title = "릴리스 노트 작성",
                        description = "다음 버전 배포를 위한 릴리스 노트를 작성하고 배포 채널에 공유합니다.",
                        tagId = 14,
                        name = "배포",
                        color = 0xFFFFC107.toInt(),
                        date = LocalDate.of(2025, 4, 29),
                        memo = "특이사항 및 알려진 이슈 포함",
                        priority = 3,
                        isDone = false,
                        createdAt = LocalDate.of(2025, 4, 23),
                        updatedAt = LocalDate.of(2025, 4, 23)
                    )
                )
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun TitleContent(
    scrollState: ScrollState,
    title: String,
    titleInputState: InputState,
    onTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var isInputFocused by remember { mutableStateOf(false) }
    val isSaveFailed = titleInputState == InputState.WARNING

    Text(
        text = "제목",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = title,
        hint = "무엇을 학습하실건가요?",
        keyboardType = KeyboardType.Text,
        onValueChange = onTitleChanged,
        limit = 20,
        rightComponent = {
            if (title.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_delete_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onTitleChanged("") },
                )
            }
        },
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .onFocusChanged { isInputFocused = it.isFocused }
            .animateScrollWhenFocus(
                scrollState = scrollState,
                verticalWeightPx = with(density) { -200.dp.roundToPx() },
            ),
    )

    EbbingVisibleAnimation(visible = isSaveFailed) {
        Text(
            text = "필수 항목을 입력해 주세요.",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.error,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun TagContent(
    tag: String,
    tagInputState: InputState,
    onTagDropDownClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSaveFailed: Boolean = (tagInputState == InputState.WARNING && tag.isEmpty())

    Text(
        text = "태그",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = tag,
        onDropDownClick = onTagDropDownClicked,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )

    EbbingVisibleAnimation(visible = isSaveFailed) {
        if (isSaveFailed) {
            Text(
                text = "필수 항목을 입력해 주세요.",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun RepeatCycleContent(
    repeatCycle: String,
    repeatCycleInputState: InputState,
    onRepeatCycleDropDownClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSaveFailed: Boolean =
        (repeatCycleInputState == InputState.WARNING && repeatCycle.isEmpty())

    Text(
        text = "반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = repeatCycle,
        onDropDownClick = onRepeatCycleDropDownClicked,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )

    EbbingVisibleAnimation(visible = isSaveFailed) {
        if (isSaveFailed) {
            Text(
                text = "필수 항목을 입력해 주세요.",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun RestDayContent(
    restDays: Set<DayOfWeek>,
    onRestDayChanged: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "쉬는 날",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        DayOfWeek.entries.forEach {
            EbbingChip(
                label = it.toKorean(),
                selected = it in restDays,
                onChipClicked = { onRestDayChanged(it) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun ScheduleContent(
    schedules: List<TodoRO>,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    EbbingVisibleAnimation(schedules.isNotEmpty()) {
        Column {
            Text(
                text = "${schedules.size} 개의 학습 일정",
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.padding(top = 32.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.light3)
            ) {
                schedules.forEachIndexed { idx, item ->
                    ScheduleCard(
                        idx = idx + 1,
                        schedule = item,
                        selectedDate = selectedDate,
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
}

@Composable
private fun ScheduleCard(
    idx: Int,
    schedule: TodoRO,
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = idx.toString(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark1,
        )

        Text(
            text = schedule.date.toFormattedString(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark1,
        )

        Text(
            text = "${daysBetween(selectedDate, schedule.date)}일 뒤",
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark1,
        )
    }
}

@Preview
@Composable
private fun PreviewAddTodo() {
    BasePreview {
        AddTodoScreen(
            selectedDate = LocalDate.now(),
            isSaveButtonEnabled = true,
            onSelectedDateChangeClick = {},
            onSaveClick = {},
            onBackClick = {},
        )
    }
}
