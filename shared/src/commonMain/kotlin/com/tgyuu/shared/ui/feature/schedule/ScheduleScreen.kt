package com.tgyuu.shared.ui.feature.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toRelativeDayDescription
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.EbbingMainTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlin.math.roundToInt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.DayOfWeek

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "일정 모아보기",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = EbbingTheme.colors.primaryDefault,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                // Selected Schedule Info
                if (state.selectedTodoInfo != null) {
                    SchedulesSection(
                        todoSchedules = state.todoSchedules,
                        selectedTodoInfo = state.selectedTodoInfo,
                        achievementRate = state.todoInfoAchievementRateMap[state.selectedTodoInfo?.id] ?: 0f,
                        onScheduleClick = { viewModel.onIntent(ScheduleIntent.OnScheduleClick(it)) },
                    )

                    HorizontalDivider(
                        color = EbbingTheme.colors.light2,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }

                // Selected Tag Info
                if (state.selectedTag != null) {
                    TodoInfosSection(
                        todoInfos = state.todoInfos,
                        selectedTag = state.selectedTag!!,
                        selectedTodoInfo = state.selectedTodoInfo,
                        achievementRateMap = state.todoInfoAchievementRateMap,
                        onTodoInfoClick = { viewModel.onIntent(ScheduleIntent.OnInfoClick(it)) },
                    )

                    HorizontalDivider(
                        color = EbbingTheme.colors.light2,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                }

                // Tags Section
                TagsSection(
                    tags = state.tags,
                    selectedTag = state.selectedTag,
                    achievementRateMap = state.tagAchievementRateMap,
                    onTagClick = { viewModel.onIntent(ScheduleIntent.OnTagClick(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TagsSection(
    tags: ImmutableList<TodoTagUiModel>,
    selectedTag: TodoTagUiModel?,
    achievementRateMap: Map<Int, Float>,
    onTagClick: (TodoTagUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(EbbingTheme.colors.light3)
                .padding(vertical = 12.dp, horizontal = 16.dp),
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
private fun TodoInfosSection(
    todoInfos: ImmutableList<TodoInfoUiModel>,
    selectedTag: TodoTagUiModel,
    selectedTodoInfo: TodoInfoUiModel?,
    achievementRateMap: Map<Int, Float>,
    onTodoInfoClick: (TodoInfoUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    append("${selectedTag.name} 태그 하위 일정 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(todoInfos.size.toString())
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f, false),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
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
private fun SchedulesSection(
    todoSchedules: ImmutableList<TodoScheduleUiModel>,
    selectedTodoInfo: TodoInfoUiModel?,
    achievementRate: Float,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    append("${selectedTodoInfo?.title ?: "선택한"} 일정 주기 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append(todoSchedules.size.toString())
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f, false),
            )

            Text(
                text = buildAnnotatedString {
                    append("달성률 ")
                    withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                        append("${(achievementRate * 100).roundToInt()}%")
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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
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
                            .padding(vertical = 12.dp),
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

    val borderModifier = if (isSelected) {
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = EbbingTheme.colors.primaryDefault,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp)
    } else {
        Modifier
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .padding(vertical = 12.dp)
    ) {
        Spacer(
            modifier = Modifier
                .clip(CircleShape)
                .size(16.dp)
                .background(Color(colorValue)),
        )

        Text(
            text = value,
            style = textStyle,
            color = EbbingTheme.colors.black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f, false)
                .padding(horizontal = 12.dp),
        )

        Text(
            text = "${(achievementRate * 100).roundToInt()}%",
            style = textStyle,
            color = EbbingTheme.colors.primaryDefault,
        )
    }
}

@Composable
private fun ScheduleCard(
    schedule: TodoScheduleUiModel,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateText = try {
        "${schedule.date.year}.${schedule.date.monthNumber}.${schedule.date.dayOfMonth} (${schedule.date.dayOfWeek.toKorean()})"
    } catch (e: Exception) {
        "날짜 오류"
    }

    val relativeText = try {
        schedule.date.toRelativeDayDescription()
    } catch (e: Exception) {
        ""
    }

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
            text = dateText,
            style = EbbingTheme.typography.bodyMSB,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.black,
        )

        Text(
            text = relativeText,
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
            .padding(vertical = 24.dp, horizontal = 16.dp),
    )
}

private fun DayOfWeek.toKorean(): String = when (this) {
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
    DayOfWeek.SUNDAY -> "일"
    else -> ""
}
