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
