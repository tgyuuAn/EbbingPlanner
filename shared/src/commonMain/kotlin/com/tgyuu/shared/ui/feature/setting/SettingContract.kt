package com.tgyuu.shared.ui.feature.setting

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE

@Immutable
data class SettingState(
    val isLoading: Boolean = false,
    val appVersion: String = "1.0.0",
    val isNotificationEnabled: Boolean = true,
    val alarmHour: Int = 18,
    val alarmMinute: Int = 30,
    val alarmMessage: String = DEFAULT_ALARM_MESSAGE,
    val alarmMessageBottomSheet: AlarmMessageBottomSheetState = AlarmMessageBottomSheetState(),
    val mondayStart: Boolean = false,
    val autoBackupFeatureEnabled: Boolean = false,
    val autoBackupEnabled: Boolean = true,
    val lastSyncTime: String? = null,
) : UiState

@Immutable
data class AlarmMessageBottomSheetState(
    val message: String = DEFAULT_ALARM_MESSAGE,
    val originMessage: String = "",
) {
    val placeholderCount: Int = "\\{할일\\}".toRegex().findAll(message).count()

    val isValidPlaceholder: Boolean = placeholderCount <= 1

    val isValidLength: Boolean = message.length <= 50

    val isValid: Boolean = isValidPlaceholder && isValidLength

    val isChanged: Boolean = message != originMessage

    val canApply: Boolean = isValid && isChanged

    val shouldShowResetButton: Boolean = message != DEFAULT_ALARM_MESSAGE
}

sealed class SettingIntent : UiIntent {
    data object OnBackClick : SettingIntent()
    data object OnTagManageClick : SettingIntent()
    data object OnRepeatCycleManageClick : SettingIntent()
    data object OnSyncClick : SettingIntent()
    data object OnRestoreByDeviceIdClick : SettingIntent()
    data object OnClearDataClick : SettingIntent()
    data object OnClearDataConfirm : SettingIntent()
    data object OnThemeClick : SettingIntent()
    data object OnNotificationClick : SettingIntent()
    data class OnNotificationToggle(val enabled: Boolean) : SettingIntent()
    data class OnUpdateAlarmTime(val hour: Int, val minute: Int) : SettingIntent()
    data object OnAlarmMessageOpen : SettingIntent()
    data class OnAlarmMessageChange(val message: String) : SettingIntent()
    data object OnAlarmMessageReset : SettingIntent()
    data object OnApplyAlarmMessage : SettingIntent()
    data object OnInAppReviewClick : SettingIntent()
    data object OnPrivacyPolicyClick : SettingIntent()
    data object OnTermsOfUseClick : SettingIntent()
    data object OnWidgetClick : SettingIntent()
    data class OnUpdateStartDay(val mondayStart: Boolean) : SettingIntent()
    data object OnAutoBackupToggleClick : SettingIntent()
    data object OnNoticeClick : SettingIntent()
    data object OnInquiryClick : SettingIntent()
}
