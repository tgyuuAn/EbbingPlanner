package com.tgyuu.home.graph.main.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.common.now
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.TodoListCard
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.displayName
import com.tgyuu.domain.model.SortType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus

private const val TODO_LIST_PAGE_COUNT = 12_001 // ±16년(일)

@Composable
internal fun EbbingTodoList(
    sortType: SortType,
    selectedDate: LocalDate,
    todoLists: List<TodoScheduleUiModel>,
    schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    onSelectDate: (LocalDate) -> Unit,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onEditScheduleClick: (TodoScheduleUiModel) -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    modifier: Modifier = Modifier,
    calendarNestedScroll: NestedScrollConnection? = null,
) {
    val initialPage = TODO_LIST_PAGE_COUNT / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { TODO_LIST_PAGE_COUNT },
    )

    var prevPage by remember { mutableIntStateOf(initialPage) }
    LaunchedEffect(pagerState.currentPage) {
        val newPage = pagerState.currentPage
        val delta = newPage - prevPage
        if (delta != 0) {
            onSelectDate(selectedDate.plus(delta.toLong(), DateTimeUnit.DAY))
            prevPage = newPage
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        TodoHeader(
            displayDate = selectedDate,
            completedCount = todoLists.count { it.isDone },
            totalCount = todoLists.size,
            sortType = sortType,
            onSortTypeChange = onSortTypeChange,
            modifier = if (calendarNestedScroll != null) {
                Modifier
                    .nestedScroll(calendarNestedScroll)
                    // 헤더 드래그를 nested scroll 이벤트로 변환 (델타는 소비하지 않음)
                    .scrollable(
                        state = rememberScrollableState { 0f },
                        orientation = Orientation.Vertical,
                    )
            } else {
                Modifier
            },
        )

        HorizontalPager(state = pagerState) { page ->
            TodoPage(
                date = selectedDate,
                todos = todoLists,
                sortType = sortType,
                schedulesByTodoInfo = schedulesByTodoInfo,
                onCheckedChange = onCheckedChange,
                onEdit = onEditScheduleClick,
            )
        }
    }
}

@Composable
private fun TodoHeader(
    displayDate: LocalDate,
    completedCount: Int,
    totalCount: Int,
    sortType: SortType,
    onSortTypeChange: (SortType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
    ) {
        val dateText = if (displayDate == LocalDate.now()) "오늘"
        else "${displayDate.monthNumber}월 ${displayDate.dayOfMonth}일"
        Text(
            text = buildAnnotatedString {
                append("$dateText 할 일 ")
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                    append(count.toString())
                }
            },
            style = EbbingTheme.typography.heading16B,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSortTypeClick() }
                .padding(end = 16.dp)
        ) {
            Text(
                text = sortType.displayName,
                style = EbbingTheme.typography.heading16B,
                color = EbbingTheme.colors.textOnBackground
            )
            Image(
                painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.textOnBackground),
                modifier = Modifier.size(20.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = EbbingTheme.colors.background,
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(EbbingTheme.colors.primaryNormal)
                .clickable { onAddTodoClick() }
                .padding(4.dp)
        )
    }
}

@Composable
private fun TodoPage(
    date: LocalDate,
    todos: List<TodoScheduleUiModel>,
    sortType: SortType,
    schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onEdit: (TodoScheduleUiModel) -> Unit,
) {
    val listState = rememberLazyListState()

    // 최신순 ↔ 태그별 전환 시 리스트를 최상단으로 부드럽게 스크롤
    LaunchedEffect(sortType) {
        listState.animateScrollToItem(0)
    }

    if (todos.isNotEmpty()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            if (sortType == SortType.BY_TAG) {
                todos.groupBy { it.name }.forEach { (tagName, group) ->
                    item(key = "tag_$tagName") {
                        TagSectionHeader(
                            tagName = tagName,
                            count = group.size,
                            modifier = Modifier.animateItem(),
                        )
                    }
                    items(items = group, key = { it.id }) { item ->
                        ScheduleCard(item, schedulesByTodoInfo, onCheckedChange, onEdit)
                    }
                }
            } else {
                items(items = todos, key = { it.id }) { item ->
                    ScheduleCard(item, schedulesByTodoInfo, onCheckedChange, onEdit)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    } else {
        Text(
            text = "${date.monthNumber}월 ${date.dayOfMonth}일 스케줄이 없어요.\n" +
                    "우측 상단 + 버튼을 눌러 새로운 스케줄을 만들어보세요.",
            style = EbbingTheme.typography.body14M,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp),
        )
    }
}

@Composable
private fun ScheduleCard(
    item: TodoScheduleUiModel,
    schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    onCheckedChange: (TodoScheduleUiModel) -> Unit,
    onEdit: (TodoScheduleUiModel) -> Unit,
) {
    TodoListCard(
        todo = item,
        todosWithSameInfo = schedulesByTodoInfo[item.infoId] ?: emptyList(),
        onCheckedChange = onCheckedChange,
        onEditScheduleClick = onEdit,
    )
}

@Composable
private fun TagSectionHeader(
    tagName: String,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
    ) {
        Text(
            text = tagName,
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textSub,
        )
        Text(
            text = count.toString(),
            style = EbbingTheme.typography.heading14SB,
            color = EbbingTheme.colors.textSub,
        )
    }
}
