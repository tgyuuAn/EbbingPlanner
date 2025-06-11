package com.tgyuu.home.graph.addtodo

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toRelativeDayDescription
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.animateScrollWhenFocus
import com.tgyuu.common.ui.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingChip
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.EbbingTextInputDropDown
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.DefaultRepeatCycles
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.addtodo.contract.AddTodoState
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
        viewModel.loadNewTag()
        viewModel.loadNewRepeatCycle()
        viewModel.loadTags()
        viewModel.loadRepeatCycles()
    }

    AddTodoScreen(
        state = state,
        onBackClick = { viewModel.onIntent(AddTodoIntent.OnBackClick) },
        onSelectedDateChangeClick = {
            viewModel.onIntent(
                AddTodoIntent.OnSelectedDataChangeClick(
                    {
                        SelectedDateBottomSheet(
                            originSelectedDate = state.selectedDate,
                            schedulesByDateMap = emptyMap(),
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
                            repeatCycleList = state.repeatCycleList,
                            originRepeatCycle = state.repeatCycle,
                            onAddRepeatCycleClick = {
                                viewModel.onIntent(AddTodoIntent.OnAddRepeatCycleClick)
                            },
                            updateRepeatCycle = {
                                viewModel.onIntent(
                                    AddTodoIntent.OnRepeatCycleChange(it)
                                )
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
    state: AddTodoState,
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
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(modifier = modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = "일정 추가",
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = "저장",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .throttledClickable(
                                throttleTime = 1500L,
                                enabled = state.isSaveEnabled
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
                            append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
                        }
                        append(" 부터\n시작하는 일정을 만들어요")
                    },
                    style = EbbingTheme.typography.headingLSB,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.clickable { onSelectedDateChangeClick() },
                )

                TitleContent(
                    scrollState = scrollState,
                    title = state.title,
                    onTitleChange = onTitleChange,
                )

                TagContent(
                    tag = state.tag,
                    onTagDropDownClick = onTagDropDownClick,
                )

                PriorityContent(
                    priority = state.priority,
                    onPriorityChange = onPriorityChange,
                )

                RepeatCycleContent(
                    repeatCycle = state.repeatCycle,
                    onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                )

                RestDayContent(
                    restDays = state.restDays,
                    onRestDayChange = onRestDayChange,
                )

                ScheduleContent(schedules = state.schedules)

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = "일정 추가",
                onNavigationClick = onBackClick,
                rightComponent = {
                    Text(
                        text = "저장",
                        style = if (state.isSaveEnabled) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
                        color = if (state.isSaveEnabled) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.dark3,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .throttledClickable(
                                throttleTime = 1500L,
                                enabled = state.isSaveEnabled
                            ) {
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
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("${state.selectedDate.monthValue}월 ${state.selectedDate.dayOfMonth}일")
                            }
                            append(" 부터\n시작하는 일정을 만들어요")
                        },
                        style = EbbingTheme.typography.headingLSB,
                        color = EbbingTheme.colors.black,
                        modifier = Modifier.clickable { onSelectedDateChangeClick() },
                    )

                    TitleContent(
                        scrollState = scrollState,
                        title = state.title,
                        onTitleChange = onTitleChange,
                    )

                    TagContent(
                        tag = state.tag,
                        onTagDropDownClick = onTagDropDownClick,
                    )

                    PriorityContent(
                        priority = state.priority,
                        onPriorityChange = onPriorityChange,
                    )

                    RepeatCycleContent(
                        repeatCycle = state.repeatCycle,
                        onRepeatCycleDropDownClick = onRepeatCycleDropDownClick,
                    )

                    RestDayContent(
                        restDays = state.restDays,
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
}

@Composable
private fun TitleContent(
    scrollState: ScrollState,
    title: String,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var isInputFocused by remember { mutableStateOf(false) }

    Text(
        text = "제목",
        style = EbbingTheme.typography.bodyMSB,
        color = EbbingTheme.colors.black,
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
        color = EbbingTheme.colors.black,
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
        color = EbbingTheme.colors.black,
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
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 32.dp),
    )

    EbbingTextInputDropDown(
        value = repeatCycle.toDisplayName(),
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
        color = EbbingTheme.colors.black,
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
                modifier = Modifier.weight(1f),
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
                color = EbbingTheme.colors.black,
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

@EbbingPreview
@Composable
private fun PreviewAddTodo() {
    BasePreview {
        AddTodoScreen(
            state = AddTodoState(
                selectedDate = LocalDate.now(),
                title = "토익",
                priority = "3",
                repeatCycle = DefaultRepeatCycles.last(),
                restDays = setOf(DayOfWeek.MONDAY),
            ),
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
