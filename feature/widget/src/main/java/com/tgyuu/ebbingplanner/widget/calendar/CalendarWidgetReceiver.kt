package com.tgyuu.ebbingplanner.widget.calendar

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.common.copy
import com.tgyuu.common.now
import com.tgyuu.designsystem.component.calendar.totalDaysInMonth
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.WIDGET_MONDAY_START
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction.Companion.TODO_ID
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CalendarWidgetReceiver : GlanceAppWidgetReceiver(), KoinComponent {
    private val todoRepository: TodoRepository by inject()
    private val configRepository: ConfigRepository by inject()
    private val analyticsHelper: AnalyticsHelper by inject()

    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    private val scope = MainScope()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "CalendarWidget", buttonName = "Set")
        )
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            RefreshAction.UPDATE_ACTION -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "CalendarWidget", buttonName = "Refresh")
                )
                updateData(context)
            }
            CheckTodoAction.CHECK_TODO_ACTION -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "CalendarWidget",
                        buttonName = "CheckTodo",
                        properties = mapOf("todoId" to todoId),
                    )
                )
                checkTodo(todoId, context)
            }
        }
    }

    private fun checkTodo(todoId: Int, context: Context) = scope.launch {
        todoRepository.toggleDone(todoId)
        updateData(context)
    }

    private fun updateData(context: Context) = scope.launch {
        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val mondayStart = configRepository.getMondayStart().firstOrNull() ?: false
        val sortType = configRepository.getSortType()

        val now = LocalDate.now()
        val allSchedules = todoRepository.loadTodoSchedulesByDateRange(
            LocalDate(now.year, now.monthNumber, 1),
            LocalDate(now.year, now.monthNumber, now.totalDaysInMonth())
        )
        val byDate = buildByDateMap(allSchedules, sortType)

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(CalendarWidget::class.java)
            .firstOrNull()

        val gson = GsonProvider.gson
        val json = gson.toJson(byDate)
        glanceId?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                pref.toMutablePreferences().apply {
                    this[SCHEDULES_BY_DATE_MAP] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                    this[WIDGET_MONDAY_START] = mondayStart
                }
            }

            glanceAppWidget.update(context, it)
        }
    }

    private fun buildByDateMap(
        schedules: List<TodoSchedule>,
        sortType: SortType,
    ): Map<LocalDate, List<TodoSchedule>> {
        val grouped = schedules.groupBy { it.date }

        return grouped.mapValues { (_, list) ->
            when (sortType) {
                SortType.CREATED -> list.sortedWith(compareBy({ it.isDone }, { it.createdAt }))
                SortType.NAME -> list.sortedWith(compareBy({ it.isDone }, { it.title }))
                SortType.PRIORITY -> list.sortedWith(compareBy({ it.isDone }, { it.priority }))
            }
        }
    }

    companion object {
        val SCHEDULES_BY_DATE_MAP = stringPreferencesKey("schedulesByDateMap")
    }
}
