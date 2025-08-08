package com.tgyuu.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toRelativeDayDescription
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.verticalScrollbar
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingMainTopBar
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoInfo
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.model.TodoTag

@Composable
internal fun ScheduleRoute(viewModel: ScheduleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadTodoSchedules()
    }

    ScheduleScreen(
        state = state,
        onTagClick = { viewModel.onIntent(ScheduleIntent.OnTagClick(it)) },
        onInfoClick = { viewModel.onIntent(ScheduleIntent.OnInfoClick(it)) },
        onScheduleClick = { viewModel.onIntent(ScheduleIntent.OnScheduleClick(it)) },
    )
}

@Composable
private fun ScheduleScreen(
    state: ScheduleState,
    onTagClick: (TodoTag) -> Unit,
    onInfoClick: (TodoInfo) -> Unit,
    onScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (windowSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneScheduleScreen(state, onTagClick, onInfoClick, onScheduleClick, modifier)
    } else {
        TabletScheduleScreen(state, onTagClick, onInfoClick, onScheduleClick, modifier)
    }
}

@Composable
private fun PhoneScheduleScreen(
    state: ScheduleState,
    onTagClick: (TodoTag) -> Unit,
    onInfoClick: (TodoInfo) -> Unit,
    onScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        EbbingMainTopBar(
            title = "일정 모아보기",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingVisibleAnimation(
            visible = state.selectedTodoInfo != null,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column {
                SchedulesBody(
                    todoSchedules = state.todoSchedules,
                    selectedTodoInfo = state.selectedTodoInfo,
                    achievementRate = state.todoInfoAchievementRateMap[state.selectedTodoInfo?.id]
                        ?: 0f,
                    onScheduleClick = onScheduleClick,
                    modifier = Modifier.heightIn(max = 400.dp),
                )

                HorizontalDivider(
                    color = EbbingTheme.colors.light2,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        EbbingVisibleAnimation(
            visible = state.selectedTag != null,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Column {
                TodoInfosBody(
                    todoInfos = state.todoInfos,
                    selectedTag = state.selectedTag!!,
                    selectedTodoInfo = state.selectedTodoInfo,
                    achievementRateMap = state.todoInfoAchievementRateMap,
                    onTodoInfoClick = onInfoClick,
                    modifier = Modifier.heightIn(max = 400.dp),
                )

                HorizontalDivider(
                    color = EbbingTheme.colors.light2,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }

        TagsBody(
            tags = state.tags,
            selectedTag = state.selectedTag,
            achievementRateMap = state.tagAchievementRateMap,
            onTagClick = onTagClick,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                .heightIn(max = 400.dp),
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun TabletScheduleScreen(
    state: ScheduleState,
    onTagClick: (TodoTag) -> Unit,
    onInfoClick: (TodoInfo) -> Unit,
    onScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxBodyHeight = (screenHeight * 0.85f)

    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "일정 모아보기",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                TagsBody(
                    tags = state.tags,
                    selectedTag = state.selectedTag,
                    achievementRateMap = state.tagAchievementRateMap,
                    onTagClick = onTagClick,
                    modifier = Modifier.heightIn(max = maxBodyHeight / 3),
                )

                EbbingVisibleAnimation(visible = state.selectedTag != null) {
                    Column {
                        HorizontalDivider(
                            color = EbbingTheme.colors.light2,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        TodoInfosBody(
                            todoInfos = state.todoInfos,
                            selectedTag = state.selectedTag!!,
                            selectedTodoInfo = state.selectedTodoInfo,
                            achievementRateMap = state.todoInfoAchievementRateMap,
                            onTodoInfoClick = onInfoClick,
                            modifier = Modifier.heightIn(max = maxBodyHeight / 2 * 3),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                EbbingVisibleAnimation(visible = state.selectedTodoInfo != null) {
                    SchedulesBody(
                        todoSchedules = state.todoSchedules,
                        selectedTodoInfo = state.selectedTodoInfo,
                        achievementRate = state.todoInfoAchievementRateMap[state.selectedTodoInfo?.id]
                            ?: 0f,
                        onScheduleClick = onScheduleClick,
                        modifier = Modifier.heightIn(max = maxBodyHeight),
                    )
                }
            }
        }
    }
}

@Composable
private fun TagsBody(
    tags: List<TodoTag>,
    selectedTag: TodoTag?,
    achievementRateMap: Map<Int, Float>,
    onTagClick: (TodoTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    append("태그 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(tags.size.toString())
                    }
                },
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = "달성률",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(end = 20.dp),
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EbbingTheme.colors.light3)
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .verticalScrollbar(
                    state = listState,
                    color = EbbingTheme.colors.light1,
                )
        ) {
            items(
                items = tags,
                key = { it.id },
            ) { tag ->
                ContentItemCard(
                    value = tag.name,
                    achievementRate = achievementRateMap[tag.id] ?: 0f,
                    colorValue = tag.color,
                    isSelected = selectedTag == tag,
                    modifier = Modifier.clickable { onTagClick(tag) },
                )
            }
        }
    }
}

@Composable
private fun TodoInfosBody(
    todoInfos: List<TodoInfo>,
    selectedTag: TodoTag,
    selectedTodoInfo: TodoInfo?,
    achievementRateMap: Map<Int, Float>,
    onTodoInfoClick: (TodoInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    append("${selectedTag.name} 태그 하위 일정 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(todoInfos.size.toString())
                    }
                },
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = "달성률",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(end = 20.dp),
            )
        }

        if (todoInfos.isEmpty()) {
            NoContentCard()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .verticalScrollbar(
                        state = listState,
                        color = EbbingTheme.colors.light1,
                    )
            ) {
                items(
                    items = todoInfos,
                    key = { it.id },
                ) { todoInfo ->
                    ContentItemCard(
                        value = todoInfo.title,
                        achievementRate = achievementRateMap[todoInfo.id] ?: 0f,
                        colorValue = selectedTag.color,
                        isSelected = selectedTodoInfo == todoInfo,
                        modifier = Modifier.clickable { onTodoInfoClick(todoInfo) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SchedulesBody(
    todoSchedules: List<TodoSchedule>,
    selectedTodoInfo: TodoInfo?,
    achievementRate: Float,
    modifier: Modifier = Modifier,
    onScheduleClick: (TodoSchedule) -> Unit,
) {
    val listState = rememberLazyListState()

    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    append("${selectedTodoInfo?.title ?: "선택한"} 일정 주기 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(todoSchedules.size.toString())
                    }
                },
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = buildAnnotatedString {
                    append("달성률 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append("${Math.round(achievementRate * 100)}%")
                    }
                },
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )
        }

        if (todoSchedules.isEmpty()) {
            NoContentCard()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .verticalScrollbar(
                        state = listState,
                        color = EbbingTheme.colors.light1,
                    )
            ) {
                items(
                    items = todoSchedules,
                    key = { it.id },
                ) { todoSchedule ->
                    ScheduleCard(
                        schedule = todoSchedule,
                        onScheduleClick = onScheduleClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentItemCard(
    value: String,
    achievementRate: Float,
    colorValue: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    val textStyle = if (isSelected) EbbingTheme.typography.bodyMSB
    else EbbingTheme.typography.bodyMM

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Spacer(
            modifier = Modifier
                .clip(CircleShape)
                .size(16.dp)
                .background(Color(colorValue))
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 1.dp,
                            color = EbbingTheme.colors.primaryDefault,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
        )

        Text(
            text = value,
            style = textStyle,
            color = EbbingTheme.colors.black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = "${Math.round(achievementRate * 100)} %",
            style = textStyle,
            color = EbbingTheme.colors.primaryDefault,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ScheduleCard(
    schedule: TodoSchedule,
    onScheduleClick: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        EbbingCheck(
            checked = schedule.isDone,
            colorValue = schedule.color,
            onCheckedChange = { onScheduleClick(schedule) },
            modifier = Modifier.size(16.dp),
        )

        Text(
            text = "${schedule.date.toFormattedString()} (${schedule.date.dayOfWeek.toKorean()})",
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = schedule.date.toRelativeDayDescription(),
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )
    }
}

@Composable
private fun NoContentCard(modifier: Modifier = Modifier) {
    Text(
        text = "선택한 항목의 하위 요소가 없습니다.",
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.black,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EbbingTheme.colors.light3)
            .padding(vertical = 24.dp, horizontal = 16.dp)
    )
}
