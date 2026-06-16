package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import java.time.ZonedDateTime

data class SettingState(
    val updateInfo: UpdateInfo? = null,
    val hardUpdateInfo: UpdateInfo? = null,
    val notificationEnabled: Boolean = true,
    val alarmHour: String = "",
    val alarmMinute: String = "",
    val alarmMessage: String = DEFAULT_ALARM_MESSAGE,
    val alarmMessageBottomSheet: AlarmMessageBottomSheetState = AlarmMessageBottomSheetState(),
    val mondayStart: Boolean = false,
    val autoBackupFeatureEnabled: Boolean = false,
    val autoBackupEnabled: Boolean = false,
    val lastSyncTime: ZonedDateTime? = null,
) : UiState

data class AlarmMessageBottomSheetState(
    val message: String = DEFAULT_ALARM_MESSAGE,
    val originMessage: String = "",
) {
    val placeholderCount: Int = Regex.escape(placeholderToken).toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= MAX_LENGTH

    val isValid: Boolean = isValidPlaceholder && isValidLength

    val isChanged: Boolean = message != originMessage

    val canApply: Boolean = isValid && isChanged

    val shouldShowResetButton: Boolean = message != DEFAULT_ALARM_MESSAGE

    companion object {
        // Structural token replaced by the to-do title at runtime; not a user-facing label.
        const val placeholderToken: String = "{할일}"
        const val MAX_LENGTH: Int = 50
    }
}
