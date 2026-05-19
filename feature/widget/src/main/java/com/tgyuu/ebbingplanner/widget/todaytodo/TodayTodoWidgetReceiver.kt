package com.tgyuu.ebbingplanner.widget.todaytodo

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO_ACTION
import com.tgyuu.ebbingplanner.widget.util.KEY_WIDGET_SOURCE
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import com.tgyuu.ebbingplanner.widget.util.WidgetUpdater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
        scope.launch {
            WidgetUpdater.updateTodayTodoWidget(context, todoRepository, configRepository)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            RefreshAction.UPDATE_ACTION -> {
                val pendingResult = goAsync()
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "TodoWidget", buttonName = "Refresh")
                )
                scope.launch {
                    try {
                        WidgetUpdater.updateTodayTodoWidget(
                            context, todoRepository, configRepository
                        )
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
            ADD_TODO_ACTION -> {
                val source = intent.extras?.getString(KEY_WIDGET_SOURCE) ?: "TodoWidget"
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = source, buttonName = "AddTodo")
                )
            }
        }
    }

    companion object {
        val TODO_LISTS = stringPreferencesKey("todoLists")
        const val MAX_VISIBLE_TODOS = 20
    }
}
