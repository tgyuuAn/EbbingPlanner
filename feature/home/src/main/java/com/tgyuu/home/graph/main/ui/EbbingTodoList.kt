package com.tgyuu.home.graph.main.ui

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingTextToggle
import com.tgyuu.designsystem.component.TodoListCard
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import com.tgyuu.designsystem.model.displayName
import com.tgyuu.domain.model.SortType
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

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
            onSelectDate(selectedDate.plusDays(delta.toLong()))
            prevPage = newPage
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        TodoHeader(
            displayDate = selectedDate,
            count = todoLists.size,
            sortType = sortType,
            onSortTypeChange = onSortTypeChange,
        )

        HorizontalPager(state = pagerState) { page ->
            TodoPage(
                date = selectedDate,
                todos = todoLists,
                sortType = sortType,
                schedulesByTodoInfo = schedulesByTodoInfo,
                onCheckedChange = onCheckedChange,
                onEdit = onEditScheduleClick
            )
        }
    }
}

@Composable
private fun TodoHeader(
    displayDate: LocalDate,
    count: Int,
    sortType: SortType,
    onSortTypeChange: (SortType) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
    ) {
        val weekday = displayDate.dayOfWeek.getDisplayName(JavaTextStyle.SHORT, Locale.getDefault())
        val dateText = stringResource(
            R.string.home_list_date,
            displayDate.monthValue,
            displayDate.dayOfMonth,
            weekday,
        )
        Text(
            text = buildAnnotatedString {
                append(dateText)
                append(" ")
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryNormal)) {
                    append(count.toString())
                }
            },
            style = EbbingTheme.typography.heading18B,
            color = EbbingTheme.colors.textOnBackground,
            modifier = Modifier.weight(1f)
        )

        EbbingTextToggle(
            firstLabel = SortType.CREATED.displayName(),
            secondLabel = SortType.BY_TAG.displayName(),
            selectedFirst = sortType == SortType.CREATED,
            onSelectedChange = { toLatest ->
                onSortTypeChange(if (toLatest) SortType.CREATED else SortType.BY_TAG)
            },
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
    onEdit: (TodoScheduleUiModel) -> Unit
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
            text = stringResource(
                R.string.home_empty_schedule,
                date.monthValue,
                date.dayOfMonth,
            ),
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
