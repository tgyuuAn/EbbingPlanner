package com.tgyuu.ebbingplanner.widget.util

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidget
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidget
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate

object WidgetUpdater {

    suspend fun updateTodayTodoWidget(
        context: Context,
        todoRepository: TodoRepository,
        configRepository: ConfigRepository,
    ) {
        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val sortType = configRepository.getSortType()
        val todoLists = todoRepository
            .loadSchedulesByDate(LocalDate.now())
            .sortedWith(sortComparator(sortType))

        withContext(Dispatchers.IO) {
            generateTodayTodoBitmaps(context, todoLists)
        }

        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(TodayTodoWidget::class.java)
        if (glanceIds.isEmpty()) return

        val json = GsonProvider.gson.toJson(todoLists)
        val widget = TodayTodoWidget()

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { pref ->
                pref.toMutablePreferences().apply {
                    this[TodayTodoWidgetReceiver.TODO_LISTS] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                }
            }
            widget.update(context, glanceId)
        }
    }

    suspend fun updateCalendarWidget(
        context: Context,
        todoRepository: TodoRepository,
        configRepository: ConfigRepository,
    ) {
        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val mondayStart = configRepository.getMondayStart().firstOrNull() ?: false
        val sortType = configRepository.getSortType()

        val now = LocalDate.now()
        val allSchedules = todoRepository.loadTodoSchedulesByDateRange(
            now.withDayOfMonth(1),
            now.withDayOfMonth(now.lengthOfMonth())
        )
        val byDate = allSchedules.groupBy { it.date }.mapValues { (_, list) ->
            list.sortedWith(sortComparator(sortType))
        }

        withContext(Dispatchers.IO) {
            generateCalendarBitmaps(context, mondayStart, now)
        }

        val glanceIds = GlanceAppWidgetManager(context)
            .getGlanceIds(CalendarWidget::class.java)
        if (glanceIds.isEmpty()) return

        val json = GsonProvider.gson.toJson(byDate)
        val widget = CalendarWidget()

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { pref ->
                pref.toMutablePreferences().apply {
                    this[CalendarWidgetReceiver.SCHEDULES_BY_DATE_MAP] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                    this[WIDGET_MONDAY_START] = mondayStart
                }
            }
            widget.update(context, glanceId)
        }
    }

    suspend fun updateAllWidgets(
        context: Context,
        todoRepository: TodoRepository,
        configRepository: ConfigRepository,
    ) {
        updateTodayTodoWidget(context, todoRepository, configRepository)
        updateCalendarWidget(context, todoRepository, configRepository)
    }

    private fun sortComparator(sortType: SortType): Comparator<TodoSchedule> =
        when (sortType) {
            SortType.CREATED -> compareBy({ it.isDone }, { it.createdAt })
            SortType.NAME -> compareBy({ it.isDone }, { it.title })
            SortType.PRIORITY -> compareBy({ it.isDone }, { it.priority })
        }

    private fun generateTodayTodoBitmaps(
        context: Context,
        todoLists: List<TodoSchedule>,
    ) {
        val white = android.graphics.Color.WHITE
        val density = context.resources.displayMetrics.density
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, TodayTodoWidgetReceiver::class.java)
        )
        val minWidgetWidthDp = widgetIds.toList().mapNotNull { id ->
            appWidgetManager.getAppWidgetOptions(id)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, -1)
                .takeIf { it > 0 }
        }.minOrNull() ?: 180
        val titleMaxWidthPx = ((minWidgetWidthDp - 99) * density).toInt().coerceAtLeast(50)

        val doneSize = todoLists.count { it.isDone }

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘 할 일   ",
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_header.png",
        )
        PretendardBitmapRenderer.renderAndSave(
            context, doneSize.toString(),
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_done_count.png",
        )
        PretendardBitmapRenderer.renderAndSave(
            context, "/${todoLists.size}",
            PretendardBitmapRenderer.Weight.BOLD, 18f, white,
            filename = "todo_total.png",
        )
        PretendardBitmapRenderer.renderAndSave(
            context, "오늘은 일정이 없어요",
            PretendardBitmapRenderer.Weight.SEMI_BOLD, 16f, white,
            filename = "todo_empty.png",
        )

        todoLists.take(TodayTodoWidgetReceiver.MAX_VISIBLE_TODOS).forEachIndexed { index, todo ->
            PretendardBitmapRenderer.renderAndSave(
                context, todo.title,
                PretendardBitmapRenderer.Weight.SEMI_BOLD, 14f, white,
                filename = "todo_title_$index.png",
                maxWidthPx = titleMaxWidthPx,
                maxLines = 1,
            )
            PretendardBitmapRenderer.renderAndSave(
                context, todo.title,
                PretendardBitmapRenderer.Weight.MEDIUM, 14f, white,
                filename = "todo_title_done_$index.png",
                maxWidthPx = titleMaxWidthPx,
                maxLines = 1,
                strikethrough = true,
            )
        }
    }

    private fun generateCalendarBitmaps(
        context: Context,
        mondayStart: Boolean,
        now: LocalDate,
    ) {
        val white = android.graphics.Color.WHITE
        PretendardBitmapRenderer.renderAndSave(
            context, "${now.year}년 ${now.monthValue}월",
            PretendardBitmapRenderer.Weight.BOLD, 16f, white,
            filename = "calendar_header.png",
        )

        com.tgyuu.designsystem.component.calendar.getEbbingDayOfWeek(mondayStart)
            .forEachIndexed { index, dow ->
                PretendardBitmapRenderer.renderAndSave(
                    context, dow.toKorean(),
                    PretendardBitmapRenderer.Weight.MEDIUM, 14f, white,
                    filename = "calendar_dow_$index.png",
                )
            }

        PretendardBitmapRenderer.renderAndSave(
            context, "오늘 할 일",
            PretendardBitmapRenderer.Weight.BOLD, 16f, white,
            filename = "calendar_section_today.png",
        )
        (1..now.lengthOfMonth()).forEach { day ->
            PretendardBitmapRenderer.renderAndSave(
                context, "${now.monthValue}월 ${day}일 할 일",
                PretendardBitmapRenderer.Weight.BOLD, 16f, white,
                filename = "calendar_section_day_$day.png",
            )
        }

        (1..31).forEach { day ->
            PretendardBitmapRenderer.renderAndSave(
                context, "$day",
                PretendardBitmapRenderer.Weight.MEDIUM, 12f, white,
                filename = "calendar_num_$day.png",
            )
        }
        PretendardBitmapRenderer.renderAndSave(
            context, "${now.dayOfMonth}",
            PretendardBitmapRenderer.Weight.BOLD, 12f, white,
            filename = "calendar_num_today.png",
        )
    }
}
