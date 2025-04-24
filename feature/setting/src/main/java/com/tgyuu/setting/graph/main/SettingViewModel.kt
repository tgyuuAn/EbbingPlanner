package com.tgyuu.setting.graph.main

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.setting.BuildConfig
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val navigationBus: NavigationBus,
) :
    BaseViewModel<SettingState, SettingIntent>(SettingState()) {
    override suspend fun processIntent(intent: SettingIntent) {
        when (intent) {
            SettingIntent.OnInquiryClick -> navigateToWebView(
                "문의하기",
                BuildConfig.EBBING_CHANNEL_TALK_URL
            )

            SettingIntent.OnNoticeClick -> navigateToWebView(
                "공지사항",
                BuildConfig.EBBING_NOTICE_URL
            )

            SettingIntent.OnPrivacyAndPolicyClick -> navigateToWebView(
                "개인정보처리방침",
                BuildConfig.EBBING_PRIVACY_AND_POLICY_URL
            )

            SettingIntent.OnTermsOfUseClick -> navigateToWebView(
                "이용약관",
                BuildConfig.EBBING_TERMS_OF_USE_URL
            )
        }
    }

    internal fun setAppVersion(version: String) = setState {
        copy(version = version)
    }

    private suspend fun navigateToWebView(title: String, url: String) =
        navigationBus.navigate(To(SettingGraph.WebViewRoute(title = title, url = url)))
}
