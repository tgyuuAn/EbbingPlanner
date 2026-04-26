package com.tgyuu.ebbingplanner.widget.util

import android.content.Context
import android.graphics.Bitmap

data class CalendarWidgetBitmaps(
    val header: Bitmap?,
    val dowList: List<Bitmap?>,
    val sectionToday: Bitmap?,
    val sectionDays: List<Bitmap?>,
    val numNormal: List<Bitmap?>,
    val numBoldToday: Bitmap?,
)

data class TodayTodoBitmaps(
    val header: Bitmap?,
    val doneCount: Bitmap?,
    val total: Bitmap?,
    val empty: Bitmap?,
    val titles: List<Bitmap?>,
)

object WidgetBitmapStore {

    fun loadCalendarBitmaps(context: Context): CalendarWidgetBitmaps =
        CalendarWidgetBitmaps(
            header = PretendardBitmapRenderer.loadBitmap(context, "calendar_header.png"),
            dowList = (0 until 7).map { i ->
                PretendardBitmapRenderer.loadBitmap(context, "calendar_dow_$i.png")
            },
            sectionToday = PretendardBitmapRenderer.loadBitmap(context, "calendar_section_today.png"),
            sectionDays = (1..31).map { day ->
                PretendardBitmapRenderer.loadBitmap(context, "calendar_section_day_$day.png")
            },
            numNormal = (1..31).map { day ->
                PretendardBitmapRenderer.loadBitmap(context, "calendar_num_$day.png")
            },
            numBoldToday = PretendardBitmapRenderer.loadBitmap(context, "calendar_num_today.png"),
        )

    fun loadTodayTodoBitmaps(context: Context, maxTodos: Int): TodayTodoBitmaps =
        TodayTodoBitmaps(
            header = PretendardBitmapRenderer.loadBitmap(context, "todo_header.png"),
            doneCount = PretendardBitmapRenderer.loadBitmap(context, "todo_done_count.png"),
            total = PretendardBitmapRenderer.loadBitmap(context, "todo_total.png"),
            empty = PretendardBitmapRenderer.loadBitmap(context, "todo_empty.png"),
            titles = (0 until maxTodos).map { i ->
                PretendardBitmapRenderer.loadBitmap(context, "todo_title_$i.png")
            },
        )
}
