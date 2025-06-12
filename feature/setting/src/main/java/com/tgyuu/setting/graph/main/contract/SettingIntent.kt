package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed interface SettingIntent : UiIntent {
    data object OnNoticeClick : SettingIntent
    data class OnAlarmTimeClick(val content: BottomSheetContent) : SettingIntent
    data class OnUpdateAlarmTime(val hour: String, val minute: String) : SettingIntent
    data object OnTagManageClick : SettingIntent
    data object OnRepeatCycleManageClick : SettingIntent
    data object OnSyncClick : SettingIntent
    data object OnPrivacyAndPolicyClick : SettingIntent
    data object OnTermsOfUseClick : SettingIntent
    data object OnInquiryClick : SettingIntent
    data object OnNotificationToggleClick : SettingIntent
}
