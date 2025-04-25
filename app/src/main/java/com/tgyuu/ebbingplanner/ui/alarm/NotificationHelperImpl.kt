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

        val contentTitle = "에빙 플래너 일정 알림"
        val contentText = "${schedules.first().title} 을 확인하고, 잊지 말고 복습하세요!"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context)
            .notify(date.hashCode(), notification)
    }
}
