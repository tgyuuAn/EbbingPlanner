package com.tgyuu.shared.ui.feature.home.notification

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

data class NotificationState(
    val isNotificationEnabled: Boolean = true,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 30,
    val alarmMessage: String = "",
    /** 로케일별 기본 문구(default_alarm_message). VM이 리소스로 채운다 */
    val defaultMessage: String = "",
    val isShowTimePicker: Boolean = false,
) : UiState

sealed interface NotificationIntent : UiIntent {
    data object OnBackClick : NotificationIntent
    data class OnNotificationToggle(val enabled: Boolean) : NotificationIntent
    data object OnTimePickerClick : NotificationIntent
    data object OnTimePickerDismiss : NotificationIntent
    data class OnTimeChange(val hour: Int, val minute: Int) : NotificationIntent
    data class OnMessageChange(val message: String) : NotificationIntent
    data object OnResetMessage : NotificationIntent
}
