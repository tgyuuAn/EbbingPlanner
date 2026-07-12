package com.tgyuu.shared.ui.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingTextToggle
import com.tgyuu.shared.designsystem.component.TodoListCard
import com.tgyuu.shared.designsystem.component.calendar.toLocalizedShort
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.model.displayName
import com.tgyuu.shared.domain.model.SortType
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.home_empty_schedule
import ebbingplanner.shared.generated.resources.home_list_date
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource

private const val TODO_LIST_PAGE_COUNT = 12_001 // ±16년(일)

@Composable
fun EbbingTodoList(
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
            onSelectDate(selectedDate.plus(delta, DateTimeUnit.DAY))
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
        )

        HorizontalPager(state = pagerState) { _ ->
            TodoPage(
                date = selectedDate,
                todos = todoLists,
                sortType = sortType,
                schedulesByTodoInfo = schedulesByTodoInfo,
                onCheckedChange = onCheckedChange,
                onEdit = onEditScheduleClick,
                calendarNestedScroll = calendarNestedScroll,
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
        val weekday = displayDate.dayOfWeek.toLocalizedShort()
        val dateText = stringResource(
            Res.string.home_list_date,
            displayDate.monthNumber,
            displayDate.dayOfMonth,
            weekday,
        )
        Text(
            text = buildAnnotatedString {
                append(dateText)
                append(" ")
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryDefault)) {
                    append(completedCount.toString())
                }
                withStyle(
                    SpanStyle(
                        color = EbbingTheme.colors.dark3,
                        fontWeight = FontWeight.Medium,
                    )
                ) {
                    append("/$totalCount")
                }
            },
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
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
    onEdit: (TodoScheduleUiModel) -> Unit,
    calendarNestedScroll: NestedScrollConnection? = null,
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
                .then(
                    if (calendarNestedScroll != null) Modifier.nestedScroll(calendarNestedScroll)
                    else Modifier
                )
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
                Res.string.home_empty_schedule,
                date.monthNumber,
                date.dayOfMonth,
            ),
            style = EbbingTheme.typography.bodySM,
            textAlign = TextAlign.Center,
            color = EbbingTheme.colors.dark3,
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
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.dark1,
        )
        Text(
            text = count.toString(),
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.dark1,
        )
    }
}
