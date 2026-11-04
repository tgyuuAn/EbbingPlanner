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

    /** 현재 OS 알림 권한 상태 (설정 화면 토글 표시/분기용, Android POST_NOTIFICATIONS 상태 대응) */
    suspend fun getPermissionStatus(): NotificationPermissionStatus
}

enum class NotificationPermissionStatus {
    GRANTED,
    NOT_DETERMINED,
    DENIED,
}
