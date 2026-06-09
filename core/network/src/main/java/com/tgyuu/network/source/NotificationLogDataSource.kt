package com.tgyuu.network.source

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class NotificationLogDataSource @Inject constructor(
    private val supabase: SupabaseClient,
) {
    suspend fun logNotificationConfig(
        uuid: String,
        enabled: Boolean,
        alarmTime: String,
        message: String,
        usesPlaceholder: Boolean,
        isDefault: Boolean,
    ) {
        supabase.from("notification_analytics")
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
}

@Serializable
private data class NotificationAnalyticsRow(
    val uuid: String,
    @SerialName("notification_enabled") val notificationEnabled: Boolean,
    @SerialName("alarm_time") val alarmTime: String,
    @SerialName("alarm_message") val alarmMessage: String,
    @SerialName("uses_placeholder") val usesPlaceholder: Boolean,
    @SerialName("is_default_message") val isDefaultMessage: Boolean,
)
