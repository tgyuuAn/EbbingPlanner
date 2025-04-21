package com.tgyuu.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen()
}

@Composable
private fun HomeScreen(modifier: Modifier = Modifier) {
    val calendarState = rememberCalendarState()

    Column(modifier = modifier) {
        EbbingCalendar(
            calendarState = calendarState,
            modifier = Modifier.fillMaxWidth(),
            onDateSelect = { selectedDate -> Log.d("test", selectedDate.toString()) },
        )
    }
}
