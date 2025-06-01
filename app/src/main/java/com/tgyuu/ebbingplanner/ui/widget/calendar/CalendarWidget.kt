package com.tgyuu.ebbingplanner.ui.widget.calendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.google.gson.reflect.TypeToken
import com.tgyuu.designsystem.component.calendar.CalendarDate
import com.tgyuu.designsystem.component.calendar.EbbingDayOfWeek
import com.tgyuu.designsystem.component.calendar.getCalendarDates
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.designsystem.foundation.DarkBackground
import com.tgyuu.designsystem.foundation.LightBackground
import com.tgyuu.designsystem.foundation.PrimaryDefault
import com.tgyuu.designsystem.foundation.PrimaryLight
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.ebbingplanner.R
import com.tgyuu.ebbingplanner.ui.MainActivity
import com.tgyuu.ebbingplanner.ui.MainActivity.Companion.ADD_TODO
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidgetReceiver.Companion.SCHEDULES_BY_DATE_MAP
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodoItemRow
import com.tgyuu.ebbingplanner.ui.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.ui.widget.util.SelectDateAction
import com.tgyuu.ebbingplanner.ui.widget.util.SelectDateAction.Companion.SELECTED_DATE
import com.tgyuu.ebbingplanner.ui.widget.util.destinationKey
import com.tgyuu.ebbingplanner.ui.widget.util.selectedDateKey
import java.time.LocalDate

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val rawJson: String = prefs[SCHEDULES_BY_DATE_MAP] ?: "[]"

            val type = object : TypeToken<Map<LocalDate, List<TodoSchedule>>>() {}.type
            val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> =
                GsonProvider.gson.fromJson(rawJson, type)

            val today = LocalDate.now()
            val calendarDates = getCalendarDates(today)

            val selectedDateString = prefs[SELECTED_DATE]
            val selectedDate = selectedDateString?.let { LocalDate.parse(it) } ?: today

            GlanceTheme {
                CalendarWidgetContent(
                    schedulesByDateMap = schedulesByDateMap,
                    selectedDate = selectedDate,
                    calendarDates = calendarDates,
                )
            }
        }
    }
}

@Composable
private fun CalendarWidgetContent(
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    calendarDates: List<CalendarDate>,
    selectedDate: LocalDate,
) {
    val selectedDateTodoLists = schedulesByDateMap[selectedDate] ?: emptyList()
    val todoListsDoneSize = selectedDateTodoLists.filter { it.isDone }.size

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(imageProvider = ImageProvider(R.drawable.shape_widget_background))
            .padding(4.dp)
    ) {
        CalendarWidgetHeader()

        CalendarWidgetBody(
            calendarDates = calendarDates,
            schedulesByDateMap = schedulesByDateMap,
            selectedDate = selectedDate,
            modifier = GlanceModifier.height(230.dp),
        )

        SelectedDateTodoList(
            selectedDate = selectedDate,
            todoLists = selectedDateTodoLists,
            doneSize = todoListsDoneSize
        )
    }
}

@Composable
private fun CalendarWidgetHeader() {
    val today = LocalDate.now()
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Text(
            text = "${today.year}년 ${today.monthValue}월",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = ColorProvider(Color.Black, Color.White),
            ),
            modifier = GlanceModifier.fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(30.dp)
        )

        Row(modifier = GlanceModifier.fillMaxWidth()) {
            EbbingDayOfWeek.forEach {
                Text(
                    text = it.toKorean(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = ColorProvider(Color.Black, Color.White),
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }
    }
}

@Composable
private fun CalendarWidgetBody(
    calendarDates: List<CalendarDate>,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    selectedDate: LocalDate,
    modifier: GlanceModifier = GlanceModifier,
) {
    val today = LocalDate.now()
    Column(modifier = modifier) {
        for (week in 0 until 6) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(vertical = 2.dp),
            ) {
                for (day in 0 until 7) {
                    val index = week * 7 + day
                    val calendarDate = calendarDates[index]
                    val schedules = schedulesByDateMap[calendarDate.date] ?: emptyList()
                    val isToday = calendarDate.date == today

                    CalendarDayCell(
                        date = calendarDate,
                        selectedDate = selectedDate,
                        schedules = schedules,
                        isToday = isToday,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    date: CalendarDate,
    selectedDate: LocalDate,
    schedules: List<TodoSchedule>,
    isToday: Boolean,
    modifier: GlanceModifier = GlanceModifier,
) {
    val isSelected = date.date == selectedDate
    val dayItemColor = if (isSelected) ColorProvider(Color.Black, Color.White)
    else ColorProvider(Color.Transparent, Color.Transparent)
    val textColor = if (isSelected) ColorProvider(Color.White, Color.Black)
    else ColorProvider(Color.Black, Color.White)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.defaultWeight()
            .cornerRadius(8.dp)
            .background(dayItemColor)
            .clickable(
                actionRunCallback<SelectDateAction>(
                    actionParametersOf(selectedDateKey to date.date.toString())
                )
            ),
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (date.isCurrentMonth) textColor
                else ColorProvider(Gray, LightGray),
                textAlign = TextAlign.Center
            )
        )

        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.padding(top = 2.dp),
        ) {
            schedules.map { it.color }
                .distinct()
                .take(4)
                .forEach { color ->
                    Spacer(
                        modifier = GlanceModifier
                            .size(6.dp)
                            .cornerRadius(999.dp)
                            .background(ColorProvider(Color(color), Color(color)))
                    )
                }
        }
    }
}

@Composable
private fun ColumnScope.SelectedDateTodoList(
    selectedDate: LocalDate,
    todoLists: List<TodoSchedule>,
    doneSize: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxWidth()
            .background(imageProvider = ImageProvider(R.drawable.shape_widget_header))
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 할 일   ",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(DarkBackground, LightBackground),
                ),
            )
            Text(
                text = doneSize.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(PrimaryDefault, PrimaryLight),
                ),
            )
            Text(
                text = " /${todoLists.size}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(DarkBackground, LightBackground),
                ),
            )
        }

        Image(
            provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_plus),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ColorProvider(DarkBackground, LightBackground)),
            modifier = GlanceModifier
                .size(20.dp)
                .clickable(
                    actionStartActivity<MainActivity>(
                        actionParametersOf(
                            destinationKey to ADD_TODO,
                            selectedDateKey to selectedDate.toString()
                        )
                    )
                ),
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier.fillMaxWidth().defaultWeight()
    ) {
        if (todoLists.isEmpty()) {
            Text(
                text = "금일 스케줄이 없어요.",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Cursive,
                    color = ColorProvider(DarkBackground, LightBackground),
                ),
            )
        } else {
            LazyColumn(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp),
            ) {
                items(items = todoLists) { item ->
                    TodoItemRow(
                        todo = item,
                        modifier = GlanceModifier.fillMaxWidth()
                            .padding(vertical = 6.dp),
                    )
                }
            }
        }
    }
}
