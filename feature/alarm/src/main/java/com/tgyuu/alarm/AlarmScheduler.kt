package com.tgyuu.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = "package:${context.packageName}".toUri()
                }
            )
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            buildPendingIntent(date)
        )
    }

    @SuppressLint("ScheduleExactAlarm")
    fun rescheduleDailyExact(
        date: LocalDate,
        newTriggerMs: Long,
    ) {
        cancelDailyExact(date)                 // 기존 알람 해제
        scheduleDailyExact(date, newTriggerMs) // 새 알람 등록
    }

    private fun cancelDailyExact(date: LocalDate) {
        alarmManager.cancel(buildPendingIntent(date))
    }

    private fun requestCodeFor(date: LocalDate): Int = date.hashCode()

    private fun buildPendingIntent(date: LocalDate): PendingIntent =
        Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra("date", date.toString())
        }.let { intent ->
            PendingIntent.getBroadcast(
                context,
                requestCodeFor(date),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
}
