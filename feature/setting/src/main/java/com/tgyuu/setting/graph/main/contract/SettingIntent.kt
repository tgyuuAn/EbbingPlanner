package com.tgyuu.setting.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed interface SettingIntent : UiIntent {
    data object OnNoticeClick : SettingIntent
    data object OnPrivacyAndPolicyClick : SettingIntent
    data object OnTermsOfUseClick : SettingIntent
    data object OnInquiryClick : SettingIntent
    data object OnNotificationToggleClick : SettingIntent
}
