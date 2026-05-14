package com.tgyuu.dashboard

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.event.EbbingEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toRelativeDayDescription
import com.tgyuu.dashboard.contract.ScheduleIntent
import com.tgyuu.dashboard.contract.ScheduleState
import com.tgyuu.dashboard.ui.bottomsheet.ScheduleDeleteBottomSheet
import com.tgyuu.dashboard.ui.bottomsheet.ScheduleDelayBottomSheet
import com.tgyuu.dashboard.ui.bottomsheet.ScheduleOptionsBottomSheet
import com.tgyuu.dashboard.ui.bottomsheet.ScheduleUpdateBottomSheet
import com.tgyuu.dashboard.ui.bottomsheet.TagEditBottomSheet
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.EbbingDialog
import com.tgyuu.designsystem.component.EbbingDialogBottom
import com.tgyuu.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.designsystem.component.EbbingMainTopBar
import com.tgyuu.designsystem.component.EbbingRoundSolidButton
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoInfoUiModel
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.domain.model.DefaultTodoTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
internal fun ScheduleRoute(viewModel: ScheduleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var deleteTagState by remember { mutableStateOf<Pair<Int, String>?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.loadTodoSchedules()
    }

    deleteTagState?.let { (tagId, tagName) ->
        DeleteTagDialog(
            tagName = tagName,
            onDismissRequest = { deleteTagState = null },
            onDeleteClick = {
                deleteTagState = null
                viewModel.onIntent(ScheduleIntent.OnDeleteTag(tagId))
            },
        )
    }

    ScheduleScreen(
        state = state,
        onToggleTagExpand = { viewModel.onIntent(ScheduleIntent.OnToggleTagExpand(it)) },
        onToggleInfoExpand = { viewModel.onIntent(ScheduleIntent.OnToggleInfoExpand(it)) },
        onScheduleClick = { viewModel.onIntent(ScheduleIntent.OnScheduleClick(it)) },
        onNavigateToAddTodo = { viewModel.onIntent(ScheduleIntent.OnNavigateToAddTodo) },
        onTagThreeDotsClick = { tagName, tagColor, tagId ->
            viewModel.onIntent(
                ScheduleIntent.OnShowBottomSheet {
                    TagEditBottomSheet(
                        originName = tagName,
                        originColor = tagColor,
                        onSave = { name, color ->
                            viewModel.onIntent(ScheduleIntent.OnSaveTag(tagId, name, color))
                        },
                        onDelete = {
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                deleteTagState = tagId to tagName
                            }
                        },
                    )
                }
            )
        },
        onScheduleThreeDotsClick = { schedule ->
            viewModel.onIntent(
                ScheduleIntent.OnShowBottomSheet {
                    ScheduleOptionsBottomSheet(
                        selectedSchedule = schedule,
                        onClickUpdate = { s ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(ScheduleIntent.OnShowBottomSheet {
                                    ScheduleUpdateBottomSheet(
                                        selectedSchedule = s,
                                        onClickUpdateInfo = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnUpdateInfoClick(
                                                    it
                                                )
                                            )
                                        },
                                        onClickUpdateDate = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnUpdateDateClick(
                                                    it
                                                )
                                            )
                                        },
                                    )
                                })
                            }
                        },
                        onClickDelete = { s ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(ScheduleIntent.OnShowBottomSheet {
                                    ScheduleDeleteBottomSheet(
                                        selectedSchedule = s,
                                        onClickDeleteSingle = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnDeleteSingleClick(
                                                    it
                                                )
                                            )
                                        },
                                        onClickDeleteRemaining = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnDeleteRemainingClick(
                                                    it
                                                )
                                            )
                                        },
                                    )
                                })
                            }
                        },
                        onClickDelay = { s ->
                            scope.launch {
                                viewModel.eventBus.sendEvent(EbbingEvent.HideBottomSheet)
                                viewModel.eventBus.awaitBottomSheetHidden()
                                viewModel.onIntent(ScheduleIntent.OnShowBottomSheet {
                                    ScheduleDelayBottomSheet(
                                        selectedSchedule = s,
                                        onClickDelaySingle = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnDelaySingleClick(
                                                    it
                                                )
                                            )
                                        },
                                        onClickDelayAll = {
                                            viewModel.onIntent(
                                                ScheduleIntent.OnDelayAllClick(
                                                    it
                                                )
                                            )
                                        },
                                    )
                                })
                            }
                        },
                        onClickMemo = { viewModel.onIntent(ScheduleIntent.OnMemoClick(it)) },
                        onClickDeleteMemo = { viewModel.onIntent(ScheduleIntent.OnDeleteMemoClick(it)) },
                    )
                }
            )
        },
    )
}

@Composable
private fun ScheduleScreen(
    state: ScheduleState,
    onToggleTagExpand: (Int) -> Unit,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onNavigateToAddTodo: () -> Unit,
    onTagThreeDotsClick: (tagName: String, tagColor: Int, tagId: Int) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (windowSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneScheduleScreen(
            state,
            onToggleTagExpand,
            onToggleInfoExpand,
            onScheduleClick,
            onNavigateToAddTodo,
            onTagThreeDotsClick,
            onScheduleThreeDotsClick,
            modifier
        )
    } else {
        TabletScheduleScreen(
            state,
            onToggleTagExpand,
            onToggleInfoExpand,
            onScheduleClick,
            onNavigateToAddTodo,
            onTagThreeDotsClick,
            onScheduleThreeDotsClick,
            modifier
        )
    }
}

@Composable
private fun PhoneScheduleScreen(
    state: ScheduleState,
    onToggleTagExpand: (Int) -> Unit,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onNavigateToAddTodo: () -> Unit,
    onTagThreeDotsClick: (tagName: String, tagColor: Int, tagId: Int) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "일정 모아보기",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(color = EbbingTheme.colors.fillStrong, thickness = 1.dp)

        if (state.visibleTags.isEmpty()) {
            EmptyScheduleContent(
                onNavigateToAddTodo = onNavigateToAddTodo,
                modifier = Modifier.weight(1f)
            )
        } else {
            TagList(
                state,
                onToggleTagExpand,
                onToggleInfoExpand,
                onScheduleClick,
                onTagThreeDotsClick,
                onScheduleThreeDotsClick,
                Modifier.weight(1f)
            )
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun TabletScheduleScreen(
    state: ScheduleState,
    onToggleTagExpand: (Int) -> Unit,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onNavigateToAddTodo: () -> Unit,
    onTagThreeDotsClick: (tagName: String, tagColor: Int, tagId: Int) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = "일정 모아보기",
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        HorizontalDivider(color = EbbingTheme.colors.fillStrong, thickness = 1.dp)

        if (state.visibleTags.isEmpty()) {
            EmptyScheduleContent(
                onNavigateToAddTodo = onNavigateToAddTodo,
                modifier = Modifier.weight(1f)
            )
        } else {
            TagList(
                state,
                onToggleTagExpand,
                onToggleInfoExpand,
                onScheduleClick,
                onTagThreeDotsClick,
                onScheduleThreeDotsClick,
                Modifier
                    .weight(1f)
                    .padding(horizontal = 100.dp)
            )
        }
    }
}

// ── Level 1: Tag List ──

@Composable
private fun TagList(
    state: ScheduleState,
    onToggleTagExpand: (Int) -> Unit,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onTagThreeDotsClick: (tagName: String, tagColor: Int, tagId: Int) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        items(items = state.visibleTags, key = { it.id }) { tag ->
            val isExpanded = tag.id in state.expandedTagIds
            val infos = state.infosByTagMap[tag.id] ?: persistentListOf()

            TagCard(
                title = tag.name,
                tagColor = tag.color,
                scheduleCount = state.tagScheduleCountMap[tag.id] ?: 0,
                achievementRate = state.tagAchievementRateMap[tag.id] ?: 0f,
                isExpanded = isExpanded,
                isDefaultTag = tag.id == DefaultTodoTag.id,
                isAllDone = state.tagAllDoneMap[tag.id] ?: false,
                infos = infos,
                state = state,
                onToggleTagExpand = { onToggleTagExpand(tag.id) },
                onToggleInfoExpand = onToggleInfoExpand,
                onScheduleClick = onScheduleClick,
                onTagThreeDotsClick = { onTagThreeDotsClick(tag.name, tag.color, tag.id) },
                onScheduleThreeDotsClick = onScheduleThreeDotsClick,
            )

            HorizontalDivider(
                color = EbbingTheme.colors.fillStrong,
                thickness = 1.dp,
            )
        }
    }
}

// ── Level 1: Tag Card ──

@Composable
private fun TagCard(
    title: String,
    tagColor: Int,
    scheduleCount: Int,
    achievementRate: Float,
    isExpanded: Boolean,
    isDefaultTag: Boolean,
    isAllDone: Boolean,
    infos: ImmutableList<TodoInfoUiModel>,
    state: ScheduleState,
    onToggleTagExpand: () -> Unit,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onTagThreeDotsClick: () -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleColor =
        if (isAllDone) EbbingTheme.colors.fillDisabled else EbbingTheme.colors.textOnBackground

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Tag header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTagExpand() }
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(tagColor))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = title,
                    style = EbbingTheme.typography.heading16B,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${scheduleCount}개의 일정, 완료율 ${Math.round(achievementRate * 100)}%",
                    style = EbbingTheme.typography.caption14R,
                    color = EbbingTheme.colors.fillDisabled,
                )
            }

            if (!isDefaultTag) {
                Image(
                    painter = painterResource(R.drawable.ic_3dots),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onTagThreeDotsClick() }
                        .padding(8.dp)
                        .size(24.dp),
                )
            }

            Image(
                painter = painterResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
                modifier = Modifier.size(24.dp),
            )
        }

        // Level 2: TodoInfo list
        if (isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EbbingTheme.colors.fillNormal)
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 8.dp),
            ) {
                infos.forEach { info ->
                    val infoExpanded = info.id in state.expandedInfoIds
                    val schedules = state.schedulesByInfoMap[info.id] ?: persistentListOf()
                    val infoAllDone = state.infoAllDoneMap[info.id] ?: false

                    TodoInfoItem(
                        title = info.title,
                        scheduleCount = state.infoScheduleCountMap[info.id] ?: 0,
                        achievementRate = state.infoAchievementRateMap[info.id] ?: 0f,
                        isExpanded = infoExpanded,
                        isAllDone = infoAllDone,
                        schedules = schedules,
                        tagColor = tagColor,
                        onToggleExpand = { onToggleInfoExpand(info.id) },
                        onScheduleClick = onScheduleClick,
                        onScheduleThreeDotsClick = onScheduleThreeDotsClick,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleTagExpand() }
                        .padding(top = 10.dp),
                ) {
                    Text(
                        text = "닫기",
                        style = EbbingTheme.typography.caption12R,
                        color = EbbingTheme.colors.textSub,
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_up),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(EbbingTheme.colors.textSub),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

// ── Level 2: TodoInfo Item ──

@Composable
private fun TodoInfoItem(
    title: String,
    scheduleCount: Int,
    achievementRate: Float,
    isExpanded: Boolean,
    isAllDone: Boolean,
    schedules: ImmutableList<TodoScheduleUiModel>,
    tagColor: Int,
    onToggleExpand: () -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleColor =
        if (isAllDone) EbbingTheme.colors.fillDisabled else EbbingTheme.colors.textOnBackground
    val accentColor =
        if (isAllDone) EbbingTheme.colors.fillDisabled else EbbingTheme.colors.primaryNormal
    val dateRange = if (schedules.isNotEmpty()) {
        val first = schedules.first().date.toFormattedString()
        val last = schedules.last().date.toFormattedString()
        "$first ~ $last"
    } else ""

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0xFF8994A8),
                spotColor = Color(0xFF8994A8),
            )
            .background(EbbingTheme.colors.background)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = titleColor)) { append(title) }
                        append("  ")
                        withStyle(SpanStyle(color = accentColor)) { append("$scheduleCount") }
                    },
                    style = EbbingTheme.typography.heading14SB,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!isExpanded && dateRange.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateRange,
                        style = EbbingTheme.typography.caption12R,
                        color = EbbingTheme.colors.fillDisabled,
                    )
                }
            }

            Text(
                text = "${Math.round(achievementRate * 100)}%",
                style = EbbingTheme.typography.heading14SB,
                color = accentColor,
            )

            Spacer(modifier = Modifier.width(4.dp))

            Image(
                painter = painterResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (isAllDone) EbbingTheme.colors.fillDisabled else EbbingTheme.colors.textOnBackground),
                modifier = Modifier.size(20.dp),
            )
        }

        // Level 3: Schedule list
        if (isExpanded) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)) {
                schedules.forEachIndexed { index, schedule ->
                    ScheduleCard(
                        index = index + 1,
                        schedule = schedule,
                        tagColor = tagColor,
                        onScheduleClick = onScheduleClick,
                        onThreeDotsClick = { onScheduleThreeDotsClick(schedule) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    if (index < schedules.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(EbbingTheme.colors.fillDisabled)
                        )
                    }
                }
            }
        }
    }
}

// ── Level 3: Schedule Card ──

@Composable
private fun ScheduleCard(
    index: Int,
    schedule: TodoScheduleUiModel,
    tagColor: Int,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onThreeDotsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateColor =
        if (schedule.isDone) EbbingTheme.colors.textSub else EbbingTheme.colors.textOnBackground
    val dateStyle =
        if (schedule.isDone) EbbingTheme.typography.body14M else EbbingTheme.typography.heading14SB
    val subColor =
        if (schedule.isDone) EbbingTheme.colors.fillDisabled else EbbingTheme.colors.textSub
    val textDecoration = if (schedule.isDone) TextDecoration.LineThrough else TextDecoration.None

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(EbbingTheme.colors.fillStrong),
        ) {
            Text(
                text = "$index",
                style = EbbingTheme.typography.caption12R,
                color = EbbingTheme.colors.textOnBackground,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${schedule.date.monthValue}월 ${schedule.date.dayOfMonth}일 (${schedule.date.dayOfWeek.toKorean()})",
            style = dateStyle,
            color = dateColor,
            textDecoration = textDecoration,
        )

        Text(
            text = schedule.date.toRelativeDayDescription(),
            style = EbbingTheme.typography.caption12R,
            color = subColor,
            textDecoration = textDecoration,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        )

        Image(
            painter = painterResource(R.drawable.ic_3dots),
            contentDescription = null,
            modifier = Modifier
                .clickable { onThreeDotsClick() }
                .padding(4.dp)
                .size(20.dp),
        )

        Spacer(modifier = Modifier.width(4.dp))

        EbbingCheck(
            checked = schedule.isDone,
            colorValue = tagColor,
            onCheckedChange = { onScheduleClick(schedule) },
            modifier = Modifier.size(20.dp),
        )
    }
}

// ── Empty State ──

@Composable
private fun EmptyScheduleContent(
    onNavigateToAddTodo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "아직 일정이 없어요.\n새로운 일정을 등록해볼까요?",
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textSub,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        EbbingRoundSolidButton(label = "일정 등록하러 가기", onClick = onNavigateToAddTodo)
    }
}

// ── Dialog ──

@Composable
private fun DeleteTagDialog(
    tagName: String,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString {
                    append("$tagName 태그를 ")
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                        append("삭제")
                    }
                    append(" 하시겠습니까?")
                },
                subText = "삭제한 태그는 되돌릴 수 없으니 신중히 선택해 주세요."
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = "뒤로",
                rightButtonText = "삭제",
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDeleteClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
