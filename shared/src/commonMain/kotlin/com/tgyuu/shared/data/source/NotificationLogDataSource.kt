package com.tgyuu.shared.data.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 알림 설정 사용 패턴 로깅 (Android core/network NotificationLogDataSource 대응).
 * Supabase notification_analytics 테이블에 uuid 기준으로 upsert 한다.
 */
interface NotificationLogDataSource {
    suspend fun logNotificationConfig(
        uuid: String,
        enabled: Boolean,
        alarmTime: String,
        message: String,
        usesPlaceholder: Boolean,
        isDefault: Boolean,
    )
}

/** Supabase 미설정 환경: 아무것도 보내지 않는다. */
class NoOpNotificationLogDataSource : NotificationLogDataSource {
    override suspend fun logNotificationConfig(
        uuid: String,
        enabled: Boolean,
        alarmTime: String,
        message: String,
        usesPlaceholder: Boolean,
        isDefault: Boolean,
    ) = Unit
}

class SupabaseNotificationLogDataSource(
    private val supabase: SupabaseClient,
) : NotificationLogDataSource {
    override suspend fun logNotificationConfig(
        uuid: String,
        enabled: Boolean,
        alarmTime: String,
        message: String,
        usesPlaceholder: Boolean,
        isDefault: Boolean,
    ) {
        supabase.from(TABLE_NOTIFICATION_ANALYTICS)
            .upsert(
                NotificationAnalyticsRow(
                    uuid = uuid,
                    notificationEnabled = enabled,
                    alarmTime = alarmTime,
                    alarmMessage = message,
                    usesPlaceholder = usesPlaceholder,
                    isDefaultMessage = isDefault,
                )
            )
    }

    private companion object {
        const val TABLE_NOTIFICATION_ANALYTICS = "notification_analytics"
    }
}

/** Android NotificationAnalyticsRow 와 같은 컬럼 (플랫폼 컬럼 없음) */
@Serializable
private data class NotificationAnalyticsRow(
    val uuid: String,
    @SerialName("notification_enabled") val notificationEnabled: Boolean,
    @SerialName("alarm_time") val alarmTime: String,
    @SerialName("alarm_message") val alarmMessage: String,
    @SerialName("uses_placeholder") val usesPlaceholder: Boolean,
    @SerialName("is_default_message") val isDefaultMessage: Boolean,
)
