package com.tgyuu.shared.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

actual class NotificationScheduler {
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    actual fun scheduleNotification(
        id: Int,
        title: String,
        message: String,
        hour: Int,
        minute: Int,
        date: LocalDate,
    ) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
        }

        val dateComponents = NSDateComponents().apply {
            setYear(date.year.toLong())
            setMonth(date.monthNumber.toLong())
            setDay(date.dayOfMonth.toLong())
            setHour(hour.toLong())
            setMinute(minute.toLong())
            setSecond(0)
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = dateComponents,
            repeats = false,
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id.toString(),
            content = content,
            trigger = trigger,
        )

        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Failed to schedule notification: ${error.localizedDescription}")
            }
        }
    }

    actual fun cancelNotification(id: Int) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id.toString()))
    }

    actual fun cancelAllNotifications() {
        center.removeAllPendingNotificationRequests()
    }

    actual suspend fun requestPermission(): Boolean = suspendCancellableCoroutine { cont ->
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
        ) { granted, error ->
            cont.resume(granted)
        }
    }
}
