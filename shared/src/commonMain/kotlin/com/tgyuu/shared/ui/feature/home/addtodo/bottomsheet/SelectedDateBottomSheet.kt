package com.tgyuu.shared.ui.feature.home.addtodo.bottomsheet

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
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.calendar.EbbingCalendar
import com.tgyuu.shared.designsystem.component.calendar.rememberCalendarState
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import kotlinx.datetime.LocalDate

@Composable
fun SelectedDateBottomSheetContent(
    originSelectedDate: LocalDate,
    schedulesByDateMap: Map<LocalDate, List<TodoScheduleUiModel>> = emptyMap(),
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var newSelectedDate by remember(originSelectedDate) { mutableStateOf(originSelectedDate) }
    val calendarState = rememberCalendarState(originSelectedDate)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        EbbingCalendar(
            calendarState = calendarState,
            schedulesByDateMap = schedulesByDateMap,
            onSelectDate = { newSelectedDate = it },
            showSyncButton = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp),
        )

        EbbingSolidButton(
            label = "적용하기",
            onClick = { onDateSelected(newSelectedDate) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
