package com.tgyuu.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDailyExact(
        date: LocalDate,
        triggerAtMillis: Long,
    ) {
        val requestCode = date.hashCode()

        val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra("date", date.toString())
        }

        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pending
        )
    }
}
