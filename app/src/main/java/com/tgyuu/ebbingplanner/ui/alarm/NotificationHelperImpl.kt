package com.tgyuu.ebbingplanner.ui.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.alarm.R
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.ebbingplanner.ui.MainActivity
import java.time.LocalDate
import javax.inject.Inject

class NotificationHelperImpl @Inject constructor() : NotificationHelper() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun showTodoNotification(
        context: Context,
        schedules: List<TodoSchedule>,
        date: LocalDate,
    ) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            date.hashCode(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (schedules.size > 1) "${schedules.first().title} 을 제외한 ${schedules.size} 개의 일정이 있어요!"
        else "${schedules.first().title} 일정이 있어요!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("오늘은 망각곡선을 갱신할 날이에요")
            .setContentText(contentText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context)
            .notify(date.hashCode(), notification)
    }
}
