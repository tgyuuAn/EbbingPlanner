package com.tgyuu.ebbingplanner.widget.calendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import com.tgyuu.designsystem.component.calendar.getEbbingDayOfWeek
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.ebbingplanner.MainActivity
import com.tgyuu.ebbingplanner.R
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver.Companion.SCHEDULES_BY_DATE_MAP
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTheme
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.ebbingplanner.widget.todaytodo.TodoItemRow
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.SelectDateAction
import com.tgyuu.ebbingplanner.widget.util.SelectDateAction.Companion.SELECTED_DATE
import com.tgyuu.ebbingplanner.widget.util.destinationKey
import com.tgyuu.ebbingplanner.widget.util.selectedDateKey
import java.time.LocalDate

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()

            val rawTheme: String = prefs[THEME] ?: Theme.NORMAL.name
            val theme = Theme.create(rawTheme)

            val backgroundAlpha: Float = prefs[BACKGROUND_ALPHA] ?: 1f
            val textAlpha: Float = prefs[TEXT_ALPHA] ?: 1f

            val rawJson: String = prefs[SCHEDULES_BY_DATE_MAP] ?: "[]"
            val type = object : TypeToken<Map<LocalDate, List<TodoSchedule>>>() {}.type
            val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> =
                GsonProvider.gson.fromJson(rawJson, type)

            val mondayStart: Boolean = prefs[WIDGET_MONDAY_START] ?: false

            val today = LocalDate.now()
            val calendarDates = getCalendarDates(today, mondayStart)

            val selectedDateString = prefs[SELECTED_DATE]
            val selectedDate = selectedDateString?.let { LocalDate.parse(it) } ?: today

            EbbingWidgetTheme(
                theme = theme,
                alpha = textAlpha,
            ) {
                CalendarWidgetContent(
                    alpha = backgroundAlpha,
                    schedulesByDateMap = schedulesByDateMap,
                    selectedDate = selectedDate,
                    calendarDates = calendarDates,
                    mondayStart = mondayStart,
                )
            }
        }
    }
}

@Composable
private fun CalendarWidgetContent(
    alpha: Float,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    calendarDates: List<CalendarDate>,
    selectedDate: LocalDate,
    mondayStart: Boolean,
) {
    val selectedDateTodoLists = schedulesByDateMap[selectedDate] ?: emptyList()
    val todoListsDoneSize = selectedDateTodoLists.filter { it.isDone }.size
    val image = when (alpha) {
        0.25f -> R.drawable.shape_widget_background_25
        0.5f -> R.drawable.shape_widget_background_25
        0.75f -> R.drawable.shape_widget_background_75
        else -> R.drawable.shape_widget_background_100
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .clickable(onClick = actionStartActivity<MainActivity>())
            .background(
                imageProvider = ImageProvider(image),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
            )
            .padding(4.dp)
    ) {
        CalendarWidgetHeader(mondayStart = mondayStart)

        CalendarWidgetBody(
            calendarDates = calendarDates,
            schedulesByDateMap = schedulesByDateMap,
            selectedDate = selectedDate,
            modifier = GlanceModifier.height(230.dp),
        )

        SelectedDateTodoList(
            alpha = alpha,
            selectedDate = selectedDate,
            todoLists = selectedDateTodoLists,
            doneSize = todoListsDoneSize
        )
    }
}

@Composable
private fun CalendarWidgetHeader(mondayStart: Boolean) {
    val today = LocalDate.now()
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(30.dp),
        ) {
            Spacer(modifier = GlanceModifier.size(20.dp))

            Text(
                text = "${today.year}년 ${today.monthValue}월",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.surface,
                ),
                modifier = GlanceModifier.defaultWeight()
            )

            Image(
                provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_return),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.surface),
                modifier = GlanceModifier
                    .size(14.dp)
                    .clickable(
                        actionRunCallback<SelectDateAction>(
                            actionParametersOf(selectedDateKey to today.toString())
                        )
                    ),
            )
        }

        Row(modifier = GlanceModifier.fillMaxWidth()) {
            getEbbingDayOfWeek(mondayStart).forEach {
                Text(
                    text = it.toKorean(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = GlanceTheme.colors.surface,
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
    val dayItemColor = if (isSelected) GlanceTheme.colors.surface
    else ColorProvider(Color.Transparent, Color.Transparent)
    val textColor = if (isSelected) GlanceTheme.colors.inverseSurface
    else GlanceTheme.colors.surface

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
                color = if (date.isCurrentMonth) textColor else GlanceTheme.colors.tertiary,
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
    alpha: Float,
    selectedDate: LocalDate,
    todoLists: List<TodoSchedule>,
    doneSize: Int,
) {
    val image = when (alpha) {
        0.25f -> R.drawable.shape_widget_header_25
        0.5f -> R.drawable.shape_widget_header_50
        0.75f -> R.drawable.shape_widget_header_75
        else -> R.drawable.shape_widget_header_100
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxWidth()
            .background(
                imageProvider = ImageProvider(image),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.surfaceVariant)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = if (selectedDate == LocalDate.now()) "오늘 할 일   "
                else "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 할 일   ",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.surface,
                ),
            )
            Text(
                text = doneSize.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.primary,
                ),
            )
            Text(
                text = " /${todoLists.size}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.surface
                ),
            )
        }

        Image(
            provider = ImageProvider(com.tgyuu.designsystem.R.drawable.ic_plus),
            contentDescription = null,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.surface),
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
                    color = GlanceTheme.colors.surface
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
