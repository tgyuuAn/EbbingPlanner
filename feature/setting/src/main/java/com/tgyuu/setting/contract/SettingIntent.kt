package com.tgyuu.setting.contract

import com.tgyuu.common.base.UiIntent

sealed class SettingIntent : UiIntent {
    data object OnNoticeClick : SettingIntent()
    data object OnPrivacyAndPolicyClick : SettingIntent()
    data object OnTermsOfUseClick : SettingIntent()
    data object OnInquiryClick : SettingIntent()
}
