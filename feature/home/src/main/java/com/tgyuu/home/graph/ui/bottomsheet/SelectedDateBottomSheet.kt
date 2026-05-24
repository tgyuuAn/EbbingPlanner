package com.tgyuu.home.graph.ui.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import java.time.LocalDate

@Composable
internal fun SelectedDateBottomSheet(
    originSelectedDate: LocalDate,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>>,
    startFromMonday: Boolean = false,
    updateSelectedDate: (LocalDate) -> Unit,
) {
    var newSelectedDate by remember(originSelectedDate) { mutableStateOf(originSelectedDate) }
    val calendarState = rememberCalendarState(originSelectedDate)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        EbbingCalendar(
            calendarState = calendarState,
            schedulesByDateMap = schedulesByDateMap,
            showSyncButton = false,
            startFromMonday = startFromMonday,
            onSelectDate = { newSelectedDate = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        )

        EbbingSolidButton(
            label = "적용하기",
            onClick = { updateSelectedDate(newSelectedDate) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
