package com.tgyuu.shared.ui.feature.setting

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

@Immutable
data class SettingState(
    val isLoading: Boolean = false,
    val appVersion: String = "1.0.0",
    val isNotificationEnabled: Boolean = true,
    val alarmTime: String = "오후 6시 30분",
) : UiState

sealed class SettingIntent : UiIntent {
    data object OnBackClick : SettingIntent()
    data object OnTagManageClick : SettingIntent()
    data object OnRepeatCycleManageClick : SettingIntent()
    data object OnSyncClick : SettingIntent()
    data object OnClearDataClick : SettingIntent()
    data object OnClearDataConfirm : SettingIntent()
    data object OnThemeClick : SettingIntent()
    data object OnNotificationClick : SettingIntent()
    data class OnNotificationToggle(val enabled: Boolean) : SettingIntent()
    data object OnInAppReviewClick : SettingIntent()
    data object OnPrivacyPolicyClick : SettingIntent()
    data object OnTermsOfUseClick : SettingIntent()
    data object OnWidgetClick : SettingIntent()
}
