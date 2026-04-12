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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    private val scope = MainScope()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        logWidgetEvent("added")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        logWidgetEvent("update")
        updateData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            RefreshAction.UPDATE_ACTION -> {
                logWidgetEvent("refresh")
                updateData(context)
            }
            CheckTodoAction.CHECK_TODO_ACTION -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                logWidgetEvent("check_todo", mapOf("todoId" to todoId))
                checkTodo(todoId, context)
            }
        }
    }

    private fun checkTodo(todoId: Int, context: Context) = scope.launch {
        val selectedTodo = todoRepository.loadSchedule(todoId)
        val updatedTodo = selectedTodo.copy(isDone = !selectedTodo.isDone)
        todoRepository.updateTodo(updatedTodo)
        updateData(context)
    }

    private fun logWidgetEvent(
        actionName: String,
        properties: Map<String, Any?>? = null,
    ) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Action(
                screenName = "CalendarWidget",
                actionName = actionName,
                properties = properties,
            )
        )
    }

    private fun updateData(context: Context) = scope.launch {
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
