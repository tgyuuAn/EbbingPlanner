package com.tgyuu.setting.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.navigation.TagGraph
import com.tgyuu.setting.BuildConfig
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val todoRepository: TodoRepository,
    private val alarmScheduler: AlarmScheduler,
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

            launch {
                configRepository.getUpdateInfo()
                    .onSuccess { setState { copy(updateInfo = it) } }
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
            SettingIntent.OnRepeatCycleManageClick ->
                navigationBus.navigate(To(RepeatCycleGraph.RepeatCycleRoute))

            SettingIntent.OnSyncClick -> navigationBus.navigate(To(SyncGraph.SyncMainRoute))
        }
    }

    private suspend fun onNotificationToggleClick() {
        configRepository.setNotificationEnabled(!currentState.notificationEnabled)
    }

    private suspend fun navigateToWebView(title: String, url: String) =
        navigationBus.navigate(To(SettingGraph.WebViewRoute(title = title, url = url)))

    private suspend fun updateAlarmTime(hour: String, minute: String) {
        configRepository.updateAlarmTime(hour, minute)

        val upcoming = todoRepository.loadUpcomingSchedules(LocalDate.now())
        val h = hour.toInt()
        val m = minute.toInt()
        val now = LocalDateTime.now()

        upcoming.forEach { sch ->
            val localDateTime = sch.date.atTime(h, m)
            if (localDateTime.isBefore(now)) return@forEach

            val trigger = localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            alarmScheduler.rescheduleDailyExact(
                date = sch.date,
                newTriggerMs = trigger
            )
        }

        setState { copy(alarmHour = hour, alarmMinute = minute) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("알람 시간을 $hour:$minute 로 변경했어요"))
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }
}
