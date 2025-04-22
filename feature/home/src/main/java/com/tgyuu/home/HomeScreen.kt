package com.tgyuu.home

import android.util.Log
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.component.EbbingCheck
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen()
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    val calendarState = rememberCalendarState()

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        EbbingCalendar(
            calendarState = calendarState,
            onDateSelect = { selectedDate -> Log.d("test", selectedDate.toString()) },
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider(
            thickness = 12.dp,
            color = EbbingTheme.colors.light3,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "오늘 할 일 3",
            style = EbbingTheme.typography.headingLSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 12.dp),
        )

        EbbingTodoList(
            todoLists = listOf("영단어 외우기", "국어 문학 공부", "지구과학 공부"),
            onCheckedChange = {},
        )
    }
}

@Composable
private fun EbbingTodoList(
    todoLists: List<String>,
    onCheckedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = todoLists,
            key = { _, item -> item },
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

@Composable
private fun TodoListCard(
    todo: String,
    onCheckedChange: (String) -> Unit,
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
                text = todo,
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
        HomeScreen()
    }
}
