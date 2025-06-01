package com.tgyuu.ebbingplanner.ui.widget.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.tgyuu.ebbingplanner.ui.MainActivity
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodayTodoWidgetReceiver

internal val destinationKey = ActionParameters.Key<String>(
    MainActivity.KEY_DESTINATION
)

internal val todoIdKey = ActionParameters.Key<Int>(
    TodayTodoWidgetReceiver.TODO_ID
)

class RefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, TodayTodoWidgetReceiver::class.java).apply {
            action = UPDATE_ACTION
        }
        context.sendBroadcast(intent)
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
        val intent = Intent(context, TodayTodoWidgetReceiver::class.java).apply {
            action = CHECK_TODO
            putExtra(TodayTodoWidgetReceiver.TODO_ID, todoId)
        }
        
        context.sendBroadcast(intent)
    }

    companion object {
        const val CHECK_TODO = "checkTodo"
    }
}
