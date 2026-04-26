package com.tgyuu.ebbingplanner.widget.calendar

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.background
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
import androidx.glance.layout.width
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
import com.tgyuu.widget.R
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver.Companion.SCHEDULES_BY_DATE_MAP
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTheme
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.EbbingWidgetTypography
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.LocalEbbingWidgetColors
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.ebbingplanner.widget.todaytodo.TodoItemRow
import com.tgyuu.ebbingplanner.widget.util.AddTodoFromWidgetAction
import com.tgyuu.ebbingplanner.widget.util.PretendardBitmapRenderer
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.SelectDateAction
import com.tgyuu.ebbingplanner.widget.util.SelectDateAction.Companion.SELECTED_DATE
import com.tgyuu.ebbingplanner.widget.util.selectedDateKey
import com.tgyuu.ebbingplanner.widget.util.widgetSourceKey
import java.time.LocalDate

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val headerBitmap = PretendardBitmapRenderer.loadBitmap(context, "calendar_header.png")
        val dowBitmaps = (0 until 7).map { i ->
            PretendardBitmapRenderer.loadBitmap(context, "calendar_dow_$i.png")
        }
        val sectionTodayBitmap = PretendardBitmapRenderer.loadBitmap(context, "calendar_section_today.png")
        val sectionDayBitmaps = (1..31).map { day ->
            PretendardBitmapRenderer.loadBitmap(context, "calendar_section_day_$day.png")
        }
        val numNormalBitmaps = (1..31).map { day ->
            PretendardBitmapRenderer.loadBitmap(context, "calendar_num_normal_$day.png")
        }
        val numDisabledBitmaps = (1..31).map { day ->
            PretendardBitmapRenderer.loadBitmap(context, "calendar_num_disabled_$day.png")
        }
        val numPrimaryBitmaps = (1..31).map { day ->
            PretendardBitmapRenderer.loadBitmap(context, "calendar_num_primary_$day.png")
        }
        val numTodayNormalBitmap = PretendardBitmapRenderer.loadBitmap(context, "calendar_num_today_normal.png")
        val numTodayPrimaryBitmap = PretendardBitmapRenderer.loadBitmap(context, "calendar_num_today_primary.png")

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
                    headerBitmap = headerBitmap,
                    dowBitmaps = dowBitmaps,
                    sectionTodayBitmap = sectionTodayBitmap,
                    sectionDayBitmaps = sectionDayBitmaps,
                    numNormalBitmaps = numNormalBitmaps,
                    numDisabledBitmaps = numDisabledBitmaps,
                    numPrimaryBitmaps = numPrimaryBitmaps,
                    numTodayNormalBitmap = numTodayNormalBitmap,
                    numTodayPrimaryBitmap = numTodayPrimaryBitmap,
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
    headerBitmap: Bitmap?,
    dowBitmaps: List<Bitmap?>,
    sectionTodayBitmap: Bitmap?,
    sectionDayBitmaps: List<Bitmap?>,
    numNormalBitmaps: List<Bitmap?>,
    numDisabledBitmaps: List<Bitmap?>,
    numPrimaryBitmaps: List<Bitmap?>,
    numTodayNormalBitmap: Bitmap?,
    numTodayPrimaryBitmap: Bitmap?,
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
            .background(
                imageProvider = ImageProvider(image),
                colorFilter = ColorFilter.tint(LocalEbbingWidgetColors.current.background)
            )
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        CalendarWidgetHeader(
            mondayStart = mondayStart,
            selectedDate = selectedDate,
            headerBitmap = headerBitmap,
            dowBitmaps = dowBitmaps,
        )

        CalendarWidgetBody(
            calendarDates = calendarDates,
            schedulesByDateMap = schedulesByDateMap,
            selectedDate = selectedDate,
            numNormalBitmaps = numNormalBitmaps,
            numDisabledBitmaps = numDisabledBitmaps,
            numPrimaryBitmaps = numPrimaryBitmaps,
            numTodayNormalBitmap = numTodayNormalBitmap,
            numTodayPrimaryBitmap = numTodayPrimaryBitmap,
        )

        SelectedDateTodoList(
            alpha = alpha,
            selectedDate = selectedDate,
            todoLists = selectedDateTodoLists,
            doneSize = todoListsDoneSize,
            sectionTodayBitmap = sectionTodayBitmap,
            sectionDayBitmaps = sectionDayBitmaps,
        )
    }
}

@Composable
private fun CalendarWidgetHeader(
    mondayStart: Boolean,
    selectedDate: LocalDate,
    headerBitmap: Bitmap?,
    dowBitmaps: List<Bitmap?>,
) {
    val today = LocalDate.now()
    Column(modifier = GlanceModifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.fillMaxWidth(),
        ) {
            if (headerBitmap != null) {
                Image(
                    provider = ImageProvider(headerBitmap),
                    contentDescription = "${today.year}년 ${today.monthValue}월",
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
            } else {
                Text(
                    text = "${today.year}년 ${today.monthValue}월",
                    style = EbbingWidgetTypography.heading16B.copy(
                        color = LocalEbbingWidgetColors.current.textOnBackground,
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                )
            }

            Image(
                provider = ImageProvider(R.drawable.ic_widget_plus),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(
                        actionRunCallback<AddTodoFromWidgetAction>(
                            actionParametersOf(
                                widgetSourceKey to "CalendarWidget",
                                selectedDateKey to selectedDate.toString()
                            )
                        )
                    ),
            )

            Spacer(modifier = GlanceModifier.size(12.dp))

            Image(
                provider = ImageProvider(R.drawable.ic_widget_return),
                contentDescription = null,
                modifier = GlanceModifier
                    .size(14.dp)
                    .clickable(
                        actionRunCallback<SelectDateAction>(
                            actionParametersOf(selectedDateKey to today.toString())
                        )
                    ),
            )
        }

        Spacer(modifier = GlanceModifier.size(12.dp))

        Row(modifier = GlanceModifier.fillMaxWidth()) {
            getEbbingDayOfWeek(mondayStart).forEachIndexed { index, dow ->
                val dowBitmap = dowBitmaps.getOrNull(index)
                if (dowBitmap != null) {
                    Image(
                        provider = ImageProvider(dowBitmap),
                        contentDescription = dow.toKorean(),
                        modifier = GlanceModifier.defaultWeight(),
                    )
                } else {
                    Text(
                        text = dow.toKorean(),
                        style = EbbingWidgetTypography.body14M.copy(
                            textAlign = TextAlign.Center,
                            color = LocalEbbingWidgetColors.current.textSub,
                        ),
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }
            }
        }

        Spacer(modifier = GlanceModifier.fillMaxWidth().height(2.dp))
        Spacer(
            modifier = GlanceModifier.fillMaxWidth()
                .height(1.dp)
                .background(LocalEbbingWidgetColors.current.fillStrong)
        )

        Spacer(modifier = GlanceModifier.fillMaxWidth().height(4.dp))
    }
}

@Composable
private fun CalendarWidgetBody(
    calendarDates: List<CalendarDate>,
    schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>,
    selectedDate: LocalDate,
    numNormalBitmaps: List<Bitmap?>,
    numDisabledBitmaps: List<Bitmap?>,
    numPrimaryBitmaps: List<Bitmap?>,
    numTodayNormalBitmap: Bitmap?,
    numTodayPrimaryBitmap: Bitmap?,
    modifier: GlanceModifier = GlanceModifier,
) {
    val today = LocalDate.now()
    val numberOfWeeks = (calendarDates.indexOfLast { it.isCurrentMonth } / 7) + 1
    Column(modifier = modifier) {
        for (week in 0 until numberOfWeeks) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(33.dp),
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
                        numNormalBitmaps = numNormalBitmaps,
                        numDisabledBitmaps = numDisabledBitmaps,
                        numPrimaryBitmaps = numPrimaryBitmaps,
                        numTodayNormalBitmap = numTodayNormalBitmap,
                        numTodayPrimaryBitmap = numTodayPrimaryBitmap,
                    )
                }
            }

            if (week != 0 || week < numberOfWeeks - 1) {
                Spacer(modifier = GlanceModifier.height(26.5.dp))
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
    numNormalBitmaps: List<Bitmap?>,
    numDisabledBitmaps: List<Bitmap?>,
    numPrimaryBitmaps: List<Bitmap?>,
    numTodayNormalBitmap: Bitmap?,
    numTodayPrimaryBitmap: Bitmap?,
    modifier: GlanceModifier = GlanceModifier,
) {
    val isSelected = date.date == selectedDate
    val dayItemColor = if (isSelected) LocalEbbingWidgetColors.current.textOnBackground
    else ColorProvider(Color.Transparent, Color.Transparent)
    val textColor = if (isSelected) LocalEbbingWidgetColors.current.textOnPrimary
    else LocalEbbingWidgetColors.current.textOnBackground

    val dayIndex = date.dayOfMonth - 1
    val dayBitmap: Bitmap? = when {
        isToday && isSelected -> numTodayPrimaryBitmap
        isToday -> numTodayNormalBitmap
        isSelected -> numPrimaryBitmaps.getOrNull(dayIndex)
        date.isCurrentMonth -> numNormalBitmaps.getOrNull(dayIndex)
        else -> numDisabledBitmaps.getOrNull(dayIndex)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.defaultWeight()
            .clickable(
                actionRunCallback<SelectDateAction>(
                    actionParametersOf(selectedDateKey to date.date.toString())
                )
            ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .size(22.dp)
                .cornerRadius(999.dp)
                .background(dayItemColor),
        ) {
            if (dayBitmap != null) {
                Image(
                    provider = ImageProvider(dayBitmap),
                    contentDescription = date.dayOfMonth.toString(),
                )
            } else {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = EbbingWidgetTypography.caption12R.copy(
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (date.isCurrentMonth) textColor else LocalEbbingWidgetColors.current.textDisabled,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }

        val dots = schedules.map { it.color }.distinct().take(3)
        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.padding(top = 2.dp),
        ) {
            dots.forEachIndexed { index, color ->
                if (index > 0) Spacer(modifier = GlanceModifier.width(2.dp))
                Spacer(
                    modifier = GlanceModifier
                        .size(7.dp)
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
    sectionTodayBitmap: Bitmap?,
    sectionDayBitmaps: List<Bitmap?>,
) {
    val today = LocalDate.now()
    val sectionHeaderBitmap: Bitmap? = if (selectedDate == today) {
        sectionTodayBitmap
    } else {
        sectionDayBitmaps.getOrNull(selectedDate.dayOfMonth - 1)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.defaultWeight()
        ) {
            if (sectionHeaderBitmap != null) {
                Image(
                    provider = ImageProvider(sectionHeaderBitmap),
                    contentDescription = if (selectedDate == today) "오늘 할 일"
                    else "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 할 일",
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
            } else {
                Text(
                    text = if (selectedDate == today) "오늘 할 일   "
                    else "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 할 일   ",
                    style = EbbingWidgetTypography.heading16B.copy(
                        color = LocalEbbingWidgetColors.current.textOnBackground,
                    ),
                )
            }
            Text(
                text = doneSize.toString(),
                style = EbbingWidgetTypography.heading16B.copy(
                    color = if (doneSize > 0) LocalEbbingWidgetColors.current.textPrimary
                    else LocalEbbingWidgetColors.current.textDisabled,
                ),
            )
            Text(
                text = "/${todoLists.size}",
                style = EbbingWidgetTypography.heading16B.copy(
                    color = LocalEbbingWidgetColors.current.textDisabled,
                ),
            )
        }
    }

    Spacer(modifier = GlanceModifier.height(12.dp))

    Box(
        contentAlignment = Alignment.TopStart,
        modifier = GlanceModifier.fillMaxWidth()
            .defaultWeight(),
    ) {
        if (todoLists.isEmpty()) {
            Text(
                text = "오늘은 일정이 없어요",
                style = EbbingWidgetTypography.heading16SB.copy(
                    textAlign = TextAlign.Start,
                    color = LocalEbbingWidgetColors.current.textSub,
                ),
            )
        } else {
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize()
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

        Spacer(modifier = GlanceModifier.height(36.dp))
    }
}
