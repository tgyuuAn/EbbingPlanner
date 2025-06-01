package com.tgyuu.ebbingplanner.ui.widget.util

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.tgyuu.ebbingplanner.ui.MainActivity
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidget
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.util.CheckTodoAction.Companion.TODO_ID
import java.time.LocalDate

internal val destinationKey = ActionParameters.Key<String>(MainActivity.KEY_DESTINATION)
internal val todoIdKey = ActionParameters.Key<Int>(TODO_ID)
internal val selectedDateKey = ActionParameters.Key<String>("selectedDate")

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
