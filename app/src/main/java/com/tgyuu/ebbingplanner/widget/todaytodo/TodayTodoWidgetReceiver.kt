package com.tgyuu.ebbingplanner.widget.todaytodo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.BACKGROUND_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.TEXT_ALPHA
import com.tgyuu.ebbingplanner.widget.designsystem.foundation.THEME
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction.Companion.TODO_ID
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO_ACTION
import com.tgyuu.ebbingplanner.widget.util.GsonProvider
import com.tgyuu.ebbingplanner.widget.util.KEY_WIDGET_SOURCE
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TodayTodoWidgetReceiver : GlanceAppWidgetReceiver() {
    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override val glanceAppWidget: GlanceAppWidget = TodayTodoWidget()

    private val scope = MainScope()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Set")
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
                    AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Refresh")
                )
                updateData(context)
            }
            CheckTodoAction.CHECK_TODO_ACTION -> {
                val todoId = intent.extras?.getInt(TODO_ID)
                todoId ?: return
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(
                        screenName = "TodoWidget",
                        buttonName = "Check",
                        properties = mapOf("todoId" to todoId),
                    )
                )
                checkTodo(todoId, context)
            }
            ADD_TODO_ACTION -> {
                val source = intent.extras?.getString(KEY_WIDGET_SOURCE) ?: "TodoWidget"
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = source, buttonName = "AddTodo")
                )
            }
        }
    }

    private fun checkTodo(todoId: Int, context: Context) = scope.launch {
        val selectedTodo = todoRepository.loadSchedule(todoId)
        val updatedTodo = selectedTodo.copy(isDone = !selectedTodo.isDone)
        todoRepository.updateTodo(updatedTodo)
        updateData(context)
    }

    private fun updateData(context: Context) = scope.launch {
        val gson = GsonProvider.gson

        val theme = configRepository.getWidgetTheme().firstOrNull() ?: Theme.NORMAL
        val backgroundAlpha = configRepository.getWidgetBackgroundAlpha().firstOrNull() ?: 1f
        val textAlpha = configRepository.getWidgetTextAlpha().firstOrNull() ?: 1f
        val todoLists = todoRepository
            .loadSchedulesByDate(LocalDate.now())
            .sortedWith(compareBy({ it.isDone }, { it.title }))

        val glanceId = GlanceAppWidgetManager(context)
            .getGlanceIds(TodayTodoWidget::class.java)
            .firstOrNull()

        val json = gson.toJson(todoLists)

        glanceId?.let {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                pref.toMutablePreferences().apply {
                    this[TODO_LISTS] = json
                    this[THEME] = theme.name
                    this[BACKGROUND_ALPHA] = backgroundAlpha
                    this[TEXT_ALPHA] = textAlpha
                }
            }

            glanceAppWidget.update(context, it)
        }
    }

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
    }
}
