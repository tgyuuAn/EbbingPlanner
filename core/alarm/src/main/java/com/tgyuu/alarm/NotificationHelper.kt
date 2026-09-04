package com.tgyuu.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.tgyuu.domain.model.TodoSchedule
import kotlinx.datetime.LocalDate

abstract class NotificationHelper() {
    fun createNotificationChannel(context: Context) {
        val mgr = context.getSystemService(NotificationManager::class.java)
        val exist = mgr.getNotificationChannel(CHANNEL_ID)
        if (exist != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(com.tgyuu.designsystem.R.string.alarm_channel_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(com.tgyuu.designsystem.R.string.alarm_channel_desc)
        }

        mgr.createNotificationChannel(channel)
    }

    abstract suspend fun showTodoNotification(
        context: Context,
        schedules: List<TodoSchedule>,
        date: LocalDate,
    )

    companion object {
        const val CHANNEL_ID = "todo_reminder"
    }
}
