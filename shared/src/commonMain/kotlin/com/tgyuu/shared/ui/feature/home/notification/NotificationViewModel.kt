package com.tgyuu.shared.ui.feature.home.notification

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_alarm_message_reset
import ebbingplanner.shared.generated.resources.snack_alarm_time_changed
import org.jetbrains.compose.resources.getString

class NotificationViewModel(
    private val configRepository: ConfigRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<NotificationState, NotificationIntent>(NotificationState()) {

    init {
        loadSettings()
    }

    private fun loadSettings() {
        safeScope.launch {
            val enabled = configRepository?.getNotificationEnabled()?.first() ?: true
            val (hour, minute) = configRepository?.getAlarmTime() ?: Pair(18, 30)
            val message = configRepository?.getAlarmMessage() ?: NotificationState.DEFAULT_ALARM_MESSAGE

            setState {
                copy(
                    isNotificationEnabled = enabled,
                    alarmHour = hour,
                    alarmMinute = minute,
                    alarmMessage = message,
                )
            }
        }
    }

    override suspend fun processIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.OnBackClick -> onNavigateBack()
            is NotificationIntent.OnNotificationToggle -> toggleNotification(intent.enabled)
            NotificationIntent.OnTimePickerClick -> setState { copy(isShowTimePicker = true) }
            NotificationIntent.OnTimePickerDismiss -> setState { copy(isShowTimePicker = false) }
            is NotificationIntent.OnTimeChange -> updateTime(intent.hour, intent.minute)
            is NotificationIntent.OnMessageChange -> updateMessage(intent.message)
            NotificationIntent.OnResetMessage -> resetMessage()
        }
    }

    private suspend fun toggleNotification(enabled: Boolean) {
        configRepository?.setNotificationEnabled(enabled)
        setState { copy(isNotificationEnabled = enabled) }
    }

    private suspend fun updateTime(hour: Int, minute: Int) {
        configRepository?.updateAlarmTime(hour.toString(), minute.toString())
        setState { copy(alarmHour = hour, alarmMinute = minute, isShowTimePicker = false) }
        onShowSnackbar(getString(Res.string.snack_alarm_time_changed))
    }

    private suspend fun updateMessage(message: String) {
        if (message.length <= 100) {
            setState { copy(alarmMessage = message) }
            configRepository?.updateAlarmMessage(message)
        }
    }

    private suspend fun resetMessage() {
        val defaultMsg = NotificationState.DEFAULT_ALARM_MESSAGE
        setState { copy(alarmMessage = defaultMsg) }
        configRepository?.updateAlarmMessage(defaultMsg)
        onShowSnackbar(getString(Res.string.snack_alarm_message_reset))
    }
}
