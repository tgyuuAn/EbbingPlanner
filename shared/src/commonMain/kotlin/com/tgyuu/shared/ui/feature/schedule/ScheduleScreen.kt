package com.tgyuu.shared.ui.feature.schedule

import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.VerticalDivider
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toRelativeDayLabel
import com.tgyuu.shared.designsystem.component.EbbingCheck
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingMainTopBar
import com.tgyuu.shared.designsystem.component.EbbingRoundSolidButton
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.shared.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.ScheduleDelayBottomSheet
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.ScheduleDeleteBottomSheet
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.ScheduleOptionsBottomSheet
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.ScheduleUpdateBottomSheet
import com.tgyuu.shared.ui.feature.schedule.bottomsheet.TagEditBottomSheet
import com.tgyuu.shared.ui.model.TodoInfoUiModel
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.schedule_card_date
import ebbingplanner.shared.generated.resources.schedule_collapse
import ebbingplanner.shared.generated.resources.schedule_empty_message
import ebbingplanner.shared.generated.resources.schedule_delete_tag_dialog_subtext
import ebbingplanner.shared.generated.resources.schedule_delete_tag_dialog_title
import ebbingplanner.shared.generated.resources.schedule_dialog_cancel
import ebbingplanner.shared.generated.resources.schedule_dialog_delete
import ebbingplanner.shared.generated.resources.schedule_empty_register_button
import ebbingplanner.shared.generated.resources.schedule_tag_count_completion
import ebbingplanner.shared.generated.resources.schedule_top_bar_title
import org.jetbrains.compose.resources.stringResource
import com.tgyuu.shared.designsystem.component.calendar.toLocalizedShort
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_3dots
import ebbingplanner.shared.generated.resources.ic_arrow_up
import ebbingplanner.shared.generated.resources.ic_arrow_down

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberEbbingBottomSheetState()

    LaunchedEffect(viewModel) {
        viewModel.loadTodoSchedules()
    }

    // 태그 삭제 확인 다이얼로그
    state.pendingDeleteTag?.let { (tagId, tagName) ->
        DeleteTagDialog(
            tagName = tagName,
            onDismissRequest = { viewModel.onIntent(ScheduleIntent.OnClearPendingDeleteTag) },
            onDeleteClick = {
                viewModel.onIntent(ScheduleIntent.OnClearPendingDeleteTag)
                viewModel.onIntent(ScheduleIntent.OnDeleteTag(tagId))
            },
        )
    }

    // 바텀시트
    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { scope.launch { bottomSheetState.hide() } },
    )

    val onTagThreeDotsClick: (String, Int, Int) -> Unit = { tagName, tagColor, tagId ->
            scope.launch {
                bottomSheetState.setBottomSheetContent {
                    TagEditBottomSheet(
                        originName = tagName,
                        originColor = tagColor,
                        onSave = { name, color ->
                            viewModel.onIntent(ScheduleIntent.OnSaveTag(tagId, name, color))
                            scope.launch { bottomSheetState.hide() }
                        },
                        onDelete = {
                            viewModel.onIntent(ScheduleIntent.OnRequestDeleteTag(tagId, tagName))
                            scope.launch { bottomSheetState.hide() }
                        },
                    )
                }
                bottomSheetState.show()
            }
        }
    val onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit = { schedule ->
            scope.launch {
                showScheduleOptionsBottomSheet(
                    schedule = schedule,
                    viewModel = viewModel,
                    bottomSheetState = bottomSheetState,
                    scope = scope,
                )
            }
        }

    val windowLayout = EbbingTheme.windowLayout
    val horizontalPadding = when {
        !windowLayout.isTablet -> 20.dp
        windowLayout.isLandscape -> 48.dp
        else -> 60.dp
    }

    if (windowLayout.isTablet && windowLayout.isLandscape && state.visibleTags.isNotEmpty()) {
        TabletLandscapeScheduleScreen(
            state = state,
            onToggleInfoExpand = { viewModel.onIntent(ScheduleIntent.OnToggleInfoExpand(it)) },
            onScheduleClick = { viewModel.onIntent(ScheduleIntent.OnScheduleClick(it)) },
            onTagThreeDotsClick = onTagThreeDotsClick,
            onScheduleThreeDotsClick = onScheduleThreeDotsClick,
            modifier = modifier,
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        EbbingMainTopBar(
            title = stringResource(Res.string.schedule_top_bar_title),
            modifier = Modifier.padding(horizontal = horizontalPadding),
        )

        HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp)

        if (state.visibleTags.isEmpty()) {
            EmptyScheduleContent(
                onNavigateToAddTodo = { viewModel.onIntent(ScheduleIntent.OnNavigateToAddTodo) },
                modifier = Modifier.weight(1f),
            )
        } else {
            TagList(
                state = state,
                onToggleTagExpand = { viewModel.onIntent(ScheduleIntent.OnToggleTagExpand(it)) },
                onToggleInfoExpand = { viewModel.onIntent(ScheduleIntent.OnToggleInfoExpand(it)) },
                onScheduleClick = { viewModel.onIntent(ScheduleIntent.OnScheduleClick(it)) },
                onTagThreeDotsClick = onTagThreeDotsClick,
                onScheduleThreeDotsClick = onScheduleThreeDotsClick,
                modifier = Modifier.weight(1f),
                horizontalPadding = horizontalPadding,
            )
        }
    }
}

/** 태블릿 가로: 왼쪽 태그 목록에서 고른 태그의 일정을 오른쪽 상세 패널에 보여줘요 (Android와 동일 수치) */
@Composable
private fun TabletLandscapeScheduleScreen(
    state: ScheduleState,
    onToggleInfoExpand: (Int) -> Unit,
    onScheduleClick: (TodoScheduleUiModel) -> Unit,
    onTagThreeDotsClick: (tagName: String, tagColor: Int, tagId: Int) -> Unit,
    onScheduleThreeDotsClick: (TodoScheduleUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTagId by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedTag = state.visibleTags.firstOrNull { it.id == selectedTagId }
        ?: state.visibleTags.first()

    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            EbbingMainTopBar(
                title = stringResource(Res.string.schedule_top_bar_title),
                modifier = Modifier.padding(horizontal = 48.dp),
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(items = state.visibleTags, key = { _, tag -> tag.id }) { idx, tag ->
                    if (idx != 0) {
                        HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                // Android fillSelected(#F1F8FE) 대응
                                if (tag.id == selectedTag.id) {
                                    Modifier.background(EbbingTheme.colors.primaryLight.copy(alpha = 0.25f))
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { selectedTagId = tag.id }
                            .padding(horizontal = 48.dp, vertical = 20.dp),
                    ) {
                        TagSummary(
                            title = tag.name,
                            tagColor = tag.color,
                            scheduleCount = state.tagScheduleCountMap[tag.id] ?: 0,
                            achievementRate = state.tagAchievementRateMap[tag.id] ?: 0f,
                            isAllDone = state.tagAllDoneMap[tag.id] ?: false,
                            modifier = Modifier.weight(1f),
                        )

                        if (tag.id != DefaultTodoTag.id) {
                            Image(
                                painter = painterResource(Res.drawable.ic_3dots),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable { onTagThreeDotsClick(tag.name, tag.color, tag.id) }
                                    .padding(8.dp)
                                    .size(24.dp),
                            )
                        }
                    }
                }

                item { HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp) }
            }
        }

        VerticalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(EbbingTheme.colors.light3),
        ) {
            item {
                TagSummary(
                    title = selectedTag.name,
                    tagColor = selectedTag.color,
                    scheduleCount = state.tagScheduleCountMap[selectedTag.id] ?: 0,
                    achievementRate = state.tagAchievementRateMap[selectedTag.id] ?: 0f,
                    isAllDone = false,
                    barWidth = 6.dp,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }

            items(items = state.infosByTagMap[selectedTag.id] ?: persistentListOf(), key = { it.id }) { info ->
                TodoInfoItem(
                    title = info.title,
                    scheduleCount = state.infoScheduleCountMap[info.id] ?: 0,
                    achievementRate = state.infoAchievementRateMap[info.id] ?: 0f,
                    isExpanded = info.id in state.expandedInfoIds,
                    isAllDone = state.infoAllDoneMap[info.id] ?: false,
                    schedules = state.schedulesByInfoMap[info.id] ?: persistentListOf(),
                    tagColor = selectedTag.color,
                    onToggleExpand = { onToggleInfoExpand(info.id) },
                    onScheduleClick = onScheduleClick,
                    onScheduleThreeDotsClick = onScheduleThreeDotsClick,
                )
            }
        }
    }
}

@Composable
private fun TagSummary(
    title: String,
    tagColor: Int,
    scheduleCount: Int,
    achievementRate: Float,
    isAllDone: Boolean,
    modifier: Modifier = Modifier,
    barWidth: Dp = 4.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Spacer(
            modifier = Modifier
                .width(barWidth)
                .height(42.dp)
                .background(Color(tagColor))
        )

        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = title,
                style = EbbingTheme.typography.headingSSB,
                color = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.schedule_tag_count_completion, scheduleCount, (achievementRate * 100).roundToInt()),
                style = EbbingTheme.typography.bodyMM,
                color = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.dark3,
            )
        }
    }
}

private suspend fun showScheduleOptionsBottomSheet(
    schedule: TodoScheduleUiModel,
    viewModel: ScheduleViewModel,
    bottomSheetState: EbbingBottomSheetState,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    bottomSheetState.setBottomSheetContent {
        ScheduleOptionsBottomSheet(
            selectedSchedule = schedule,
            onClickUpdate = { s ->
                scope.launch {
                    bottomSheetState.hide()
                    bottomSheetState.setBottomSheetContent {
                        ScheduleUpdateBottomSheet(
                            selectedSchedule = s,
                            onClickUpdateInfo = {
                                viewModel.onIntent(ScheduleIntent.OnUpdateInfoClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                            onClickUpdateDate = {
                                viewModel.onIntent(ScheduleIntent.OnUpdateDateClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                        )
                    }
                    bottomSheetState.show()
                }
            },
            onClickDelete = { s ->
                scope.launch {
                    bottomSheetState.hide()
                    bottomSheetState.setBottomSheetContent {
                        ScheduleDeleteBottomSheet(
                            selectedSchedule = s,
                            onClickDeleteSingle = {
                                viewModel.onIntent(ScheduleIntent.OnDeleteSingleClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                            onClickDeleteRemaining = {
                                viewModel.onIntent(ScheduleIntent.OnDeleteRemainingClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                        )
                    }
                    bottomSheetState.show()
                }
            },
            onClickDelay = { s ->
                scope.launch {
                    bottomSheetState.hide()
                    bottomSheetState.setBottomSheetContent {
                        ScheduleDelayBottomSheet(
                            selectedSchedule = s,
                            onClickDelaySingle = {
                                viewModel.onIntent(ScheduleIntent.OnDelaySingleClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                            onClickDelayAll = {
                                viewModel.onIntent(ScheduleIntent.OnDelayAllClick(it))
                                scope.launch { bottomSheetState.hide() }
                            },
                        )
                    }
                    bottomSheetState.show()
                }
            },
            onClickMemo = {
                viewModel.onIntent(ScheduleIntent.OnMemoClick(it))
                scope.launch { bottomSheetState.hide() }
            },
            onClickDeleteMemo = {
                viewModel.onIntent(ScheduleIntent.OnDeleteMemoClick(it))
                scope.launch { bottomSheetState.hide() }
            },
        )
    }
    bottomSheetState.show()
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
    horizontalPadding: Dp = 20.dp,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items = state.visibleTags, key = { _, tag -> tag.id }) { idx, tag ->
            val isExpanded = tag.id in state.expandedTagIds
            val infos = state.infosByTagMap[tag.id] ?: persistentListOf()

            if (idx != 0) {
                HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp)
            }

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
                horizontalPadding = horizontalPadding,
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
    horizontalPadding: Dp = 20.dp,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Tag header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTagExpand() }
                .padding(horizontal = horizontalPadding, vertical = 20.dp),
        ) {
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .height(42.dp)
                    .background(Color(tagColor))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    style = EbbingTheme.typography.headingSSB,
                    color = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(Res.string.schedule_tag_count_completion, scheduleCount, (achievementRate * 100).roundToInt()),
                    style = EbbingTheme.typography.bodyMM,
                    color = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.dark3,
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (!isDefaultTag) {
                Image(
                    painter = painterResource(Res.drawable.ic_3dots),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onTagThreeDotsClick() }
                        .padding(8.dp)
                        .size(24.dp),
                )
            }

            Icon(
                painter = if (isExpanded) painterResource(Res.drawable.ic_arrow_up) else painterResource(Res.drawable.ic_arrow_down),
                contentDescription = null,
                tint = EbbingTheme.colors.black,
                modifier = Modifier.size(24.dp),
            )
        }

        // Level 2: TodoInfo list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                HorizontalDivider(color = EbbingTheme.colors.light2, thickness = 1.dp)

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EbbingTheme.colors.light3)
                        .padding(horizontal = horizontalPadding - 4.dp)
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
                            text = stringResource(Res.string.schedule_collapse),
                            style = EbbingTheme.typography.bodySSB,
                            color = EbbingTheme.colors.dark2,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_up),
                            contentDescription = null,
                            tint = EbbingTheme.colors.dark2,
                            modifier = Modifier.size(16.dp),
                        )
                    }
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
    val titleColor = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.black
    val accentColor = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.primaryDefault
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = titleColor)) { append(title) }
                        append("  ")
                        withStyle(SpanStyle(color = accentColor)) { append("$scheduleCount") }
                    },
                    style = EbbingTheme.typography.headingMB,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "${(achievementRate * 100).roundToInt()}%",
                    style = EbbingTheme.typography.bodySSB,
                    color = accentColor,
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = if (isExpanded) painterResource(Res.drawable.ic_arrow_up) else painterResource(Res.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.black,
                    modifier = Modifier.size(20.dp),
                )
            }
            AnimatedVisibility(
                visible = !isExpanded && dateRange.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateRange,
                        style = EbbingTheme.typography.bodySM,
                        color = if (isAllDone) EbbingTheme.colors.light1 else EbbingTheme.colors.dark3,
                    )
                }
            }
        }

        // Level 3: Schedule list
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)) {
                schedules.forEachIndexed { index, schedule ->
                    ScheduleCard(
                        index = index + 1,
                        schedule = schedule,
                        tagColor = tagColor,
                        onScheduleClick = onScheduleClick,
                        onThreeDotsClick = { onScheduleThreeDotsClick(schedule) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (index < schedules.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(EbbingTheme.colors.light2)
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
    val dateColor = if (schedule.isDone) EbbingTheme.colors.dark3 else EbbingTheme.colors.black
    val dateStyle = if (schedule.isDone) EbbingTheme.typography.bodySM else EbbingTheme.typography.bodySSB
    val subColor = if (schedule.isDone) EbbingTheme.colors.light1 else EbbingTheme.colors.dark3
    val textDecoration = if (schedule.isDone) TextDecoration.LineThrough else TextDecoration.None

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(EbbingTheme.colors.light3),
        ) {
            Text(
                text = "$index",
                style = EbbingTheme.typography.captionR12,
                color = EbbingTheme.colors.black,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(Res.string.schedule_card_date, schedule.date.monthNumber, schedule.date.dayOfMonth, schedule.date.dayOfWeek.toLocalizedShort()),
            style = dateStyle,
            color = dateColor,
            textDecoration = textDecoration,
        )

        Text(
            text = schedule.date.toRelativeDayLabel(),
            style = EbbingTheme.typography.captionR12,
            color = subColor,
            textDecoration = textDecoration,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        )

        Image(
            painter = painterResource(Res.drawable.ic_3dots),
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
            text = stringResource(Res.string.schedule_empty_message),
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.dark3,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        EbbingRoundSolidButton(label = stringResource(Res.string.schedule_empty_register_button), onClick = onNavigateToAddTodo)
    }
}

// ── Dialog ──

@Composable
private fun DeleteTagDialog(
    tagName: String,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    // Android 일정 화면은 태그 화면과 달리 하이라이트 없는 단일 문구를 쓴다.
    val dialogTitle = stringResource(Res.string.schedule_delete_tag_dialog_title, tagName)
    val dialogSubText = stringResource(Res.string.schedule_delete_tag_dialog_subtext)

    EbbingDialog(
        dialogTop = {
            EbbingDialogDefaultTop(
                title = buildAnnotatedString { append(dialogTitle) },
                subText = dialogSubText,
            )
        },
        dialogBottom = {
            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.schedule_dialog_cancel),
                rightButtonText = stringResource(Res.string.schedule_dialog_delete),
                onLeftButtonClick = onDismissRequest,
                onRightButtonClick = onDeleteClick,
            )
        },
        onDismissRequest = onDismissRequest,
    )
}
