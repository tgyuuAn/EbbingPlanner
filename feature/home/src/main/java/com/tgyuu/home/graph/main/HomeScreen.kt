package com.tgyuu.home.graph.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.home.graph.main.contract.HomeIntent.OnAddTodoClick
import java.time.LocalDate

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadSchedules()
    }

    if (state.isLoading) {

    } else {
        HomeScreen(
            schedulesByDateMap = state.schedulesByDateMap,
            onAddTodoClick = { viewModel.onIntent(OnAddTodoClick(it)) },
        )
    }
}

@Composable
private fun HomeScreen(
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    onAddTodoClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val calendarState = rememberCalendarState()

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        EbbingCalendar(
            calendarState = calendarState,
            onDateSelect = { selectedDate = it },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider(
            thickness = 12.dp,
            color = EbbingTheme.colors.light3,
            modifier = Modifier.fillMaxWidth()
        )

        EbbingTodoList(
            todoLists = schedulesByDateMap[selectedDate] ?: emptyList(),
            onAddTodoClick = { onAddTodoClick(selectedDate) },
            onCheckedChange = {},
        )
    }
}

@Composable
private fun EbbingTodoList(
    todoLists: List<TodoSchedule>,
    onCheckedChange: (TodoSchedule) -> Unit,
    onAddTodoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 12.dp),
        ) {
            Text(
                text = "오늘 할 일 ${todoLists.size}",
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = EbbingTheme.colors.white,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(EbbingTheme.colors.primaryDefault)
                    .clickable { onAddTodoClick() },
            )
        }

        LazyColumn(modifier = modifier.fillMaxSize()) {
            itemsIndexed(
                items = todoLists,
                key = { idx, item -> item.id },
            ) { idx, item ->
                TodoListCard(
                    todo = item,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 24.dp,
                    )
                )

                if (idx < todoLists.size - 1) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = EbbingTheme.colors.light2,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

@Composable
private fun TodoListCard(
    todo: TodoSchedule,
    onCheckedChange: (TodoSchedule) -> Unit,
    modifier: Modifier = Modifier,
) {
    var checked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = todo.title,
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.dark1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }

        EbbingCheck(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.size(30.dp),
        )
    }
}

@Preview
@Composable
private fun Preview1() {
    BasePreview {
        HomeScreen(
            schedulesByDateMap = emptyMap(),
            onAddTodoClick = {},
        )
    }
}
