package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed interface SettingIntent : UiIntent {
    data object OnNoticeClick : SettingIntent
    data class OnAlarmTimeClick(val content: BottomSheetContent) : SettingIntent
    data class OnUpdateAlarmTime(val hour: String, val minute: String) : SettingIntent
    data class OnAlarmMessageClick(val content: BottomSheetContent) : SettingIntent
    data class OnAlarmMessageChange(val message: String) : SettingIntent
    data object OnAlarmMessageReset : SettingIntent
    data object OnApplyAlarmMessage : SettingIntent
    data object OnTagManageClick : SettingIntent
    data object OnRepeatCycleManageClick : SettingIntent
    data object OnSyncClick : SettingIntent
    data object OnClearClick : SettingIntent
    data object OnAppThemeManageClick : SettingIntent
    data object OnWidgetManageClick : SettingIntent
    data object OnPrivacyAndPolicyClick : SettingIntent
    data object OnTermsOfUseClick : SettingIntent
    data object OnNotificationToggleClick : SettingIntent
    data object OnInAppReviewClick : SettingIntent
    data class OnUpdateClick(val isImmediateUpdate: Boolean) : SettingIntent
    data object OnMondayStartToggleClick : SettingIntent
}
