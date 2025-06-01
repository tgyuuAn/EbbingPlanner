package com.tgyuu.ebbingplanner.ui.widget.calendar

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.google.gson.reflect.TypeToken
import com.tgyuu.designsystem.component.calendar.CalendarDate
import com.tgyuu.designsystem.component.calendar.EbbingDayOfWeek
import com.tgyuu.designsystem.component.calendar.getCalendarDates
import com.tgyuu.designsystem.component.calendar.toKorean
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.ebbingplanner.R
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidgetReceiver.Companion.SCHEDULES_BY_DATE_MAP
import com.tgyuu.ebbingplanner.ui.widget.util.GsonProvider
import java.time.LocalDate

class CalendarWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val rawJson: String = prefs[SCHEDULES_BY_DATE_MAP] ?: "[]"

            val type = object : TypeToken<Map<LocalDate, List<TodoSchedule>>>() {}.type
            val schedulesByDateMap: Map<LocalDate, List<TodoSchedule>> =
                GsonProvider.gson.fromJson(rawJson, type)

            GlanceTheme { CalendarWidgetContent(schedulesByDateMap) }
        }
    }
}

@Composable
private fun CalendarWidgetContent(schedulesByDateMap: Map<LocalDate, List<TodoSchedule>>) {
    val currentDate = LocalDate.now()
    val calendarDates = getCalendarDates(currentDate)
    val today = LocalDate.now()

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(imageProvider = ImageProvider(R.drawable.shape_widget_background))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "${currentDate.year}년 ${currentDate.monthValue}월",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.fillMaxWidth()
                .height(40.dp),
        )

        // 요일 헤더
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            EbbingDayOfWeek.forEach {
                Text(
                    text = it.toKorean(),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }

        // 날짜 셀 6주 x 7일
        Column(modifier = GlanceModifier.defaultWeight()) {
            for (week in 0 until 6) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 6.dp),
                ) {
                    for (day in 0 until 7) {
                        val index = week * 7 + day
                        val calendarDate = calendarDates[index]
                        val schedules = schedulesByDateMap[calendarDate.date] ?: emptyList()
                        val isToday = calendarDate.date == today

                        CalendarDayCell(
                            date = calendarDate,
                            schedules = schedules,
                            isToday = isToday,
                            modifier = GlanceModifier.padding(horizontal = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    date: CalendarDate,
    schedules: List<TodoSchedule>,
    isToday: Boolean,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.defaultWeight()
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (date.isCurrentMonth) ColorProvider(Black, Gray)
                else ColorProvider(Gray, Gray),
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
