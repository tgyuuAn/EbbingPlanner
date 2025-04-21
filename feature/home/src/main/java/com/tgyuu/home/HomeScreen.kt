package com.tgyuu.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
            style = EbbingTheme.typography.headingMSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp),
        )

        EbbingTodoList(
            todoLists = listOf("a", "b", "c"),
            onSuccessClick = {},
        )
    }
}

@Composable
private fun EbbingTodoList(
    todoLists: List<String>,
    onSuccessClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(
            items = todoLists,
            key = { _, item -> item },
        ) { idx, item ->
            TodoListCard(
                todo = item,
                onSuccessClick = onSuccessClick,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 24.dp,
                )
            )

            if (idx < todoLists.size - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = EbbingTheme.colors.light3,
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
    onSuccessClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = todo,
            style = EbbingTheme.typography.headingMSB,
            color = EbbingTheme.colors.dark1,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}
