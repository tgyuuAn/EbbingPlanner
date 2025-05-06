package com.tgyuu.setting.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.TagGraph
import com.tgyuu.setting.BuildConfig
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<SettingState, SettingIntent>(SettingState()) {

    init {
        viewModelScope.launch {
            launch {
                val (hour, minute) = configRepository.getAlarmTime()
                setState {
                    copy(
                        alarmHour = hour.toString().padStart(2, '0'),
                        alarmMinute = minute.toString().padStart(2, '0'),
                    )
                }
            }

            configRepository.getNotificationEnabled()
                .collect { setState { copy(notificationEnabled = it) } }
        }
    }

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

            SettingIntent.OnNotificationToggleClick -> onNotificationToggleClick()
            is SettingIntent.OnAlarmTimeClick ->
                eventBus.sendEvent(EbbingEvent.ShowBottomSheet(intent.content))

            SettingIntent.OnTagManageClick -> navigationBus.navigate(To(TagGraph.TagRoute))
            is SettingIntent.OnUpdateAlarmTime -> updateAlarmTime(intent.hour, intent.minute)
        }
    }

    internal fun setAppVersion(version: String) = setState {
        copy(version = version)
    }

    private suspend fun onNotificationToggleClick() {
        configRepository.setNotificationEnabled(!currentState.notificationEnabled)
    }

    private suspend fun navigateToWebView(title: String, url: String) =
        navigationBus.navigate(To(SettingGraph.WebViewRoute(title = title, url = url)))

    private suspend fun updateAlarmTime(hour: String, minute: String) {
        configRepository.updateAlarmTime(hour, minute)
        setState { copy(alarmHour = hour, alarmMinute = minute) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("알람 시간을 수정하였습니다"))
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }
}
