package com.tgyuu.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate
import javax.inject.Inject

abstract class NotificationHelper @Inject constructor() {
    fun createNotificationChannel(context: Context) {
        val mgr = context.getSystemService(NotificationManager::class.java)
        val exist = mgr.getNotificationChannel(CHANNEL_ID)
        if (exist != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply { description = CHANNEL_DESC }

        mgr.createNotificationChannel(channel)
    }

    abstract fun showTodoNotification(
        context: Context,
        schedules: List<TodoSchedule>,
        date: LocalDate,
    )

    companion object {
        const val CHANNEL_ID = "todo_reminder"
        const val CHANNEL_NAME = "할 일 알림"
        const val CHANNEL_DESC = "에빙 플래너 일정 알림 채널"
    }
}
