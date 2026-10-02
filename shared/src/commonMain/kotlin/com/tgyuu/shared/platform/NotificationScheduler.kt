package com.tgyuu.shared.platform

import kotlinx.datetime.LocalDate

expect class NotificationScheduler {
    fun scheduleNotification(
        id: Int,
        title: String,
        message: String,
        hour: Int,
        minute: Int,
        date: LocalDate,
    )

    fun cancelNotification(id: Int)

    fun cancelAllNotifications()

    suspend fun requestPermission(): Boolean
}
