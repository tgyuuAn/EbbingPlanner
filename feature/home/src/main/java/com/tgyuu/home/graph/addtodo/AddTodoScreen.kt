package com.tgyuu.home.graph.addtodo

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
import androidx.compose.runtime.LaunchedEffect
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
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toRelativeDayDescription
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
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.ui.InputState
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.RepeatCycleBottomSheet
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.SelectedDateBottomSheet
import com.tgyuu.home.graph.addtodo.ui.bottomsheet.TagBottomSheet
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
internal fun AddTodoRoute(
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadTags()
    }

    AddTodoScreen(
        selectedDate = state.selectedDate,
        title = state.title,
        priority = state.priority,
        repeatCycle = state.repeatCycle,
        restDays = state.restDays,
        tag = state.tag,
        isSaveEnabled = state.isSaveEnabled,
        schedules = state.schedules,
        onBackClick = { viewModel.onIntent(AddTodoIntent.OnBackClick) },
        onSelectedDateChangeClick = {
            viewModel.onIntent(
                AddTodoIntent.OnSelectedDataChangeClick(
                    {
                        SelectedDateBottomSheet(
                            originSelectedDate = state.selectedDate,
                            updateSelectedDate = {
                                viewModel.onIntent(
                                    AddTodoIntent.OnSelectedDateChange(it)
                                )
                            },
                        )
                    }
                )
            )
        },
        onTitleChange = { viewModel.onIntent(AddTodoIntent.OnTitleChange(it)) },
        onPriorityChange = { viewModel.onIntent(AddTodoIntent.OnPriorityChange(it)) },
        onTagDropDownClick = {
            viewModel.onIntent(
                AddTodoIntent.OnTagDropDownClick(
                    {
                        TagBottomSheet(
                            originTag = state.tag,
                            tagList = state.tagList,
                            updateTag = { viewModel.onIntent(AddTodoIntent.OnTagChange(it)) },
                            onAddTagClick = { viewModel.onIntent(AddTodoIntent.OnAddTagClick) },
                        )
                    }
                )
            )
        },
        onRepeatCycleDropDownClick = {
            viewModel.onIntent(
                AddTodoIntent.OnRepeatCycleDropDownClick(
                    {
                        RepeatCycleBottomSheet(
                            originRepeatCycle = state.repeatCycle,
                            updateRepeatCycle = {
                                viewModel.onIntent(AddTodoIntent.OnRepeatCycleChange(it))
                            },
                        )
                    }
                )
            )
        },
        onRestDayChange = { viewModel.onIntent(AddTodoIntent.OnRestDayChange(it)) },
        onSaveClick = { viewModel.onIntent(AddTodoIntent.OnSaveClick) },
    )
}

@Composable
private fun AddTodoScreen(
    selectedDate: LocalDate,
    title: String,
    priority: String?,
    repeatCycle: RepeatCycle,
    restDays: Set<DayOfWeek>,
    tag: TodoTag?,
    isSaveEnabled: Boolean,
    schedules: List<LocalDate>,
    onBackClick: () -> Unit,
    onSelectedDateChangeClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onTagDropDownClick: () -> Unit,
    onRepeatCycleDropDownClick: () -> Unit,
    onRestDayChange: (DayOfWeek) -> Unit,
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
                    style = if (isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                    color = if (isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                    modifier = Modifier.throttledClickable(
                        throttleTime = 1500L,
                        enabled = isSaveEnabled
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
                    append(" 부터\n시작하는 일정을 만들어요.")
                },
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.clickable { onSelectedDateChangeClick() },
            )

            TitleContent(
                scrollState = scrollState,
                title = title,
                titleInputState = InputState.DEFAULT,
                onTitleChange = onTitleChange,
            )

            TagContent(
                tag = tag,
                onTagDropDownClick = onTagDropDownClick,
            )

            PriorityContent(
                priority = priority,
                onPriorityChange = onPriorityChange,
            )

            RepeatCycleContent(
                repeatCycle = repeatCycle,
                onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
            )

            RestDayContent(
                restDays = restDays,
                onRestDayChange = onRestDayChange,
            )

            ScheduleContent(schedules = schedules)

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun TitleContent(
    scrollState: ScrollState,
    title: String,
    titleInputState: InputState,
    onTitleChange: (String) -> Unit,
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
        onValueChange = onTitleChange,
        limit = 20,
        rightComponent = {
            if (title.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_delete_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable { onTitleChange("") },
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
    tag: TodoTag?,
    onTagDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "태그",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = tag?.name ?: "",
        color = tag?.color,
        onDropDownClick = onTagDropDownClick,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
private fun PriorityContent(
    priority: String?,
    onPriorityChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "우선순위",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDefault(
        value = priority ?: "",
        onValueChange = onPriorityChange,
        hint = "얼마나 중요한 일정인가요?",
        keyboardType = KeyboardType.Number,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}


@Composable
private fun RepeatCycleContent(
    repeatCycle: RepeatCycle,
    onRepeatCycleDropDownClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "반복 주기",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.dark1,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = repeatCycle.displayName,
        onDropDownClick = onRepeatCycleDropDownClick,
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
    )
}

@Composable
private fun RestDayContent(
    restDays: Set<DayOfWeek>,
    onRestDayChange: (DayOfWeek) -> Unit,
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
                onChipClicked = { onRestDayChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun ScheduleContent(
    schedules: List<LocalDate>,
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
    schedule: LocalDate,
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
            text = "${schedule.toFormattedString()} (${schedule.dayOfWeek.toKorean()})",
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark1,
        )

        Text(
            text = schedule.toRelativeDayDescription(),
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
            title = "토익",
            priority = "3",
            repeatCycle = RepeatCycle.D1_7_15_30_60,
            restDays = setOf(DayOfWeek.MONDAY),
            tag = null,
            schedules = RepeatCycle.D1_7_15_30_60.intervals.map {
                LocalDate.now().plusDays(it.toLong())
            },
            isSaveEnabled = true,
            onSelectedDateChangeClick = {},
            onSaveClick = {},
            onBackClick = {},
            onTitleChange = {},
            onPriorityChange = {},
            onTagDropDownClick = {},
            onRepeatCycleDropDownClick = {},
            onRestDayChange = {},
        )
    }
}
