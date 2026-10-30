package com.tgyuu.shared.ui.feature.home.notification

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.repository.ConfigRepository
import ebbingplanner.shared.generated.resources.default_alarm_message
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
    private val alarmRescheduler: AlarmRescheduler? = null,
) : BaseViewModel<NotificationState, NotificationIntent>(NotificationState()) {

    init {
        loadSettings()
    }

    private fun loadSettings() {
        safeScope.launch {
            val enabled = configRepository?.getNotificationEnabled()?.first() ?: true
            val (hour, minute) = configRepository?.getAlarmTime() ?: Pair(18, 30)
            // 저장 sentinel은 로케일별 기본 문구로 표시 (Android 동일)
            val defaultMessage = getString(Res.string.default_alarm_message)
            val stored = configRepository?.getAlarmMessage().orEmpty()
            val message = if (stored.isEmpty() || stored == ConfigRepository.DEFAULT_ALARM_MESSAGE) defaultMessage else stored

            setState {
                copy(
                    isNotificationEnabled = enabled,
                    alarmHour = hour,
                    alarmMinute = minute,
                    alarmMessage = message,
                    defaultMessage = defaultMessage,
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
        if (enabled) alarmRescheduler?.rescheduleAll() else alarmRescheduler?.cancelAll()
    }

    private suspend fun updateTime(hour: Int, minute: Int) {
        configRepository?.updateAlarmTime(hour.toString(), minute.toString())
        alarmRescheduler?.rescheduleAll()
        setState { copy(alarmHour = hour, alarmMinute = minute, isShowTimePicker = false) }
        onShowSnackbar(getString(Res.string.snack_alarm_time_changed))
    }

    private suspend fun updateMessage(message: String) {
        if (message.length <= 100) {
            setState { copy(alarmMessage = message) }
            configRepository?.updateAlarmMessage(message)
            alarmRescheduler?.rescheduleAll()
        }
    }

    private suspend fun resetMessage() {
        val defaultMsg = currentState.defaultMessage.ifEmpty { getString(Res.string.default_alarm_message) }
        setState { copy(alarmMessage = defaultMsg) }
        configRepository?.updateAlarmMessage(defaultMsg)
        alarmRescheduler?.rescheduleAll()
        onShowSnackbar(getString(Res.string.snack_alarm_message_reset))
    }
}
