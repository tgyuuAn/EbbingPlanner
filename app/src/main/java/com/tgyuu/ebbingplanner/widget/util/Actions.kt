package com.tgyuu.ebbingplanner.widget.util

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.ebbingplanner.MainActivity
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidget
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.ebbingplanner.widget.util.CheckTodoAction.Companion.TODO_ID
import java.time.LocalDate

const val KEY_DESTINATION = "destination"
const val KEY_SELECTED_DATE = "selectedDate"
const val ADD_TODO = "addTodo"
const val ADD_TODO_ACTION = "addTodoAction"
const val KEY_WIDGET_SOURCE = "widgetSource"

internal val destinationKey = ActionParameters.Key<String>(KEY_DESTINATION)
internal val todoIdKey = ActionParameters.Key<Int>(TODO_ID)
internal val selectedDateKey = ActionParameters.Key<String>(KEY_SELECTED_DATE)
internal val widgetSourceKey = ActionParameters.Key<String>(KEY_WIDGET_SOURCE)

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val todayTodoIntent = Intent(context, TodayTodoWidgetReceiver::class.java).apply {
            action = UPDATE_ACTION
        }
        context.sendBroadcast(todayTodoIntent)

        val calendarIntent = Intent(context, CalendarWidgetReceiver::class.java).apply {
            action = UPDATE_ACTION
        }
        context.sendBroadcast(calendarIntent)
    }

    companion object {
        const val UPDATE_ACTION = "updateAction"
    }
}

class CheckTodoAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val todoId: Int = parameters[todoIdKey] ?: return
        val todayTodoIntent = Intent(context, TodayTodoWidgetReceiver::class.java).apply {
            action = CHECK_TODO_ACTION
            putExtra(TODO_ID, todoId)
        }

        context.sendBroadcast(todayTodoIntent)

        val calendarIntent = Intent(context, CalendarWidgetReceiver::class.java).apply {
            action = CHECK_TODO_ACTION
            putExtra(TODO_ID, todoId)
        }
        context.sendBroadcast(calendarIntent)
    }

    companion object {
        const val TODO_ID = "todoId"
        const val CHECK_TODO_ACTION = "checkTodo"
    }
}

class SelectDateAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val selectedDate = parameters[selectedDateKey] ?: return
        val date = LocalDate.parse(selectedDate)
        val today = LocalDate.now()

        // 이번 달이 아니라면 보여주지 않음
        if (date.monthValue != today.monthValue) return

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[SELECTED_DATE] = date.toString()
            }
        }

        CalendarWidget().update(context, glanceId)
    }

    companion object {
        val SELECTED_DATE = stringPreferencesKey("selectedDate")
    }
}

class AddTodoFromWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val widgetSource = parameters[widgetSourceKey] ?: "TodoWidget"
        val selectedDate = parameters[selectedDateKey]

        val broadcastIntent = Intent(context, TodayTodoWidgetReceiver::class.java).apply {
            action = ADD_TODO_ACTION
            putExtra(KEY_WIDGET_SOURCE, widgetSource)
        }
        context.sendBroadcast(broadcastIntent)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(KEY_DESTINATION, ADD_TODO)
            if (selectedDate != null) putExtra(KEY_SELECTED_DATE, selectedDate)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(activityIntent)
    }
}
