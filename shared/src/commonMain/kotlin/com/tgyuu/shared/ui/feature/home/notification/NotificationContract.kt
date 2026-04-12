package com.tgyuu.shared.ui.feature.home.notification

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

data class NotificationState(
    val isNotificationEnabled: Boolean = true,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 30,
    val alarmMessage: String = DEFAULT_ALARM_MESSAGE,
    val isShowTimePicker: Boolean = false,
) : UiState {
    val formattedAlarmTime: String
        get() {
            val period = if (alarmHour < 12) "오전" else "오후"
            val displayHour = if (alarmHour % 12 == 0) 12 else alarmHour % 12
            return "$period ${displayHour}시 ${alarmMinute.toString().padStart(2, '0')}분"
        }

    val previewMessage: String
        get() = alarmMessage.replace("{할일}", "영어 단어 복습")

    companion object {
        const val DEFAULT_ALARM_MESSAGE = "{할일} 을 확인하고, 잊지 말고 복습하세요!"
    }
}

sealed interface NotificationIntent : UiIntent {
    data object OnBackClick : NotificationIntent
    data class OnNotificationToggle(val enabled: Boolean) : NotificationIntent
    data object OnTimePickerClick : NotificationIntent
    data object OnTimePickerDismiss : NotificationIntent
    data class OnTimeChange(val hour: Int, val minute: Int) : NotificationIntent
    data class OnMessageChange(val message: String) : NotificationIntent
    data object OnResetMessage : NotificationIntent
}
