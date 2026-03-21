package com.tgyuu.setting.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.now
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.inappreview.InAppReviewManager
import com.tgyuu.inappupdate.InAppUpdateManager
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.navigation.TagGraph
import com.tgyuu.setting.BuildConfig
import com.tgyuu.setting.graph.main.contract.AlarmMessageBottomSheetState
import com.tgyuu.setting.graph.main.contract.SettingIntent
import com.tgyuu.setting.graph.main.contract.SettingSideEffect
import com.tgyuu.setting.graph.main.contract.SettingState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

class SettingViewModel(
    private val configRepository: ConfigRepository,
    private val todoRepository: TodoRepository,
    private val alarmScheduler: AlarmScheduler,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    val inAppReviewManager: InAppReviewManager,
    val inAppUpdateManager: InAppUpdateManager,
) : BaseViewModel<SettingState, SettingIntent>(SettingState()) {

    private val _sideEffect = Channel<SettingSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

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
                val message = configRepository.getAlarmMessage()
                setState { copy(alarmMessage = message) }
            }

            launch {
                suspendRunCatching {
                    configRepository.getSoftUpdateInfo()
                }.onSuccess { setState { copy(updateInfo = it) } }
            }

            launch {
                suspendRunCatching {
                    configRepository.getHardUpdateInfo()
                }.onSuccess { setState { copy(hardUpdateInfo = it) } }
            }

            launch {
                configRepository.getMondayStart()
                    .collect { setState { copy(mondayStart = it) } }
            }

            configRepository.getNotificationEnabled()
                .collect { setState { copy(notificationEnabled = it) } }
        }
    }

    override suspend fun processIntent(intent: SettingIntent) {
        when (intent) {
            SettingIntent.OnNoticeClick -> onNoticeClick()
            SettingIntent.OnPrivacyAndPolicyClick -> onPrivacyAndPolicyClick()
            SettingIntent.OnTermsOfUseClick -> onTermsOfUseClick()
            SettingIntent.OnNotificationToggleClick -> onNotificationToggleClick()
            is SettingIntent.OnAlarmTimeClick -> onAlarmTimeClick(intent.content)
            is SettingIntent.OnAlarmMessageClick -> onAlarmMessageClick(intent.content)
            is SettingIntent.OnAlarmMessageChange -> onAlarmMessageChange(intent.message)
            SettingIntent.OnAlarmMessageReset -> onAlarmMessageReset()
            SettingIntent.OnApplyAlarmMessage -> applyAlarmMessage()
            SettingIntent.OnTagManageClick -> onTagManageClick()
            is SettingIntent.OnUpdateAlarmTime -> updateAlarmTime(intent.hour, intent.minute)
            SettingIntent.OnRepeatCycleManageClick -> onRepeatCycleManageClick()
            SettingIntent.OnSyncClick -> onSyncClick()
            SettingIntent.OnAppThemeManageClick -> onAppThemeManageClick()
            SettingIntent.OnWidgetManageClick -> onWidgetManageClick()
            SettingIntent.OnClearClick -> onClearClick()
            SettingIntent.OnInAppReviewClick -> onInAppReviewClick()
            is SettingIntent.OnUpdateClick -> onUpdateClick(intent.isImmediateUpdate)
            is SettingIntent.OnStartDayClick -> onStartDayClick(intent.content)
            is SettingIntent.OnUpdateStartDay -> onUpdateStartDay(intent.mondayStart)
        }
    }

    private suspend fun onNoticeClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Notice")
        )
        navigateToWebView("공지사항", BuildConfig.EBBING_NOTICE_URL)
    }

    private suspend fun onPrivacyAndPolicyClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "PrivacyAndPolicy")
        )
        navigateToWebView("개인정보처리방침", BuildConfig.EBBING_PRIVACY_AND_POLICY_URL)
    }

    private suspend fun onTermsOfUseClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "TermsOfUse")
        )
        navigateToWebView("이용약관", BuildConfig.EBBING_TERMS_OF_USE_URL)
    }

    private suspend fun onNotificationToggleClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "NotificationNudge_Toggle")
        )
        configRepository.setNotificationEnabled(!currentState.notificationEnabled)
    }

    private suspend fun onAlarmTimeClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "NotificationNudge_Time")
        )
        eventBus.sendEvent(EbbingEvent.ShowBottomSheet(content))
    }

    private suspend fun onAlarmMessageClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "NotificationMessage")
        )
        setState {
            copy(
                alarmMessageBottomSheet = AlarmMessageBottomSheetState(
                    message = alarmMessage.ifEmpty { DEFAULT_ALARM_MESSAGE },
                    originMessage = alarmMessage.ifEmpty { DEFAULT_ALARM_MESSAGE },
                )
            )
        }
        eventBus.sendEvent(EbbingEvent.ShowBottomSheet(content))
    }

    private fun onAlarmMessageChange(message: String) {
        setState {
            copy(alarmMessageBottomSheet = alarmMessageBottomSheet.copy(message = message))
        }
    }

    private fun onAlarmMessageReset() {
        setState {
            copy(
                alarmMessageBottomSheet = alarmMessageBottomSheet.copy(
                    message = DEFAULT_ALARM_MESSAGE
                )
            )
        }
    }

    private suspend fun onTagManageClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ManageTag")
        )
        navigationBus.navigate(To(TagGraph.TagRoute))
    }

    private suspend fun onRepeatCycleManageClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ManageRepeatCycle")
        )
        navigationBus.navigate(To(RepeatCycleGraph.RepeatCycleRoute))
    }

    private suspend fun onSyncClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "SyncData")
        )
        navigationBus.navigate(To(SyncGraph.SyncMainRoute))
    }

    private suspend fun onAppThemeManageClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditAppTheme")
        )
        navigationBus.navigate(To(SettingGraph.ThemeRoute))
    }

    private suspend fun onWidgetManageClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditWidgetTheme")
        )
        navigationBus.navigate(To(SettingGraph.WidgetRoute))
    }

    private fun onClearClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "ClearData")
        )
        clearData()
    }

    private suspend fun onInAppReviewClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "InAppReview")
        )
        _sideEffect.send(SettingSideEffect.RequestInAppReview)
    }

    private suspend fun onUpdateClick(isImmediateUpdate: Boolean) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Update")
        )
        _sideEffect.send(SettingSideEffect.RequestInAppUpdate(isImmediateUpdate))
    }

    private suspend fun onStartDayClick(content: BottomSheetContent) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "StartDay")
        )
        eventBus.sendEvent(EbbingEvent.ShowBottomSheet(content))
    }

    private suspend fun onUpdateStartDay(mondayStart: Boolean) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = SCREEN_NAME,
                buttonName = "ApplyStartDay",
                properties = mapOf("monday_start" to mondayStart),
            )
        )
        configRepository.setMondayStart(mondayStart)
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }

    private suspend fun navigateToWebView(title: String, url: String) =
        navigationBus.navigate(To(SettingGraph.WebViewRoute(title = title, url = url)))

    @OptIn(ExperimentalTime::class)
    private suspend fun updateAlarmTime(hour: String, minute: String) {
        configRepository.updateAlarmTime(hour, minute)

        val upcoming = todoRepository.loadUpcomingSchedules(LocalDate.now())
        val h = hour.toInt()
        val m = minute.toInt()
        val now = LocalDateTime.now()

        upcoming.forEach { sch ->
            val localDateTime = sch.date.run {
                LocalDateTime(
                    year = year,
                    month = month,
                    dayOfMonth = dayOfMonth,
                    hour = h,
                    minute = m,
                )
            }
            if (localDateTime < now) return@forEach

            val trigger = localDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

            alarmScheduler.rescheduleDailyExact(
                date = sch.date, newTriggerMs = trigger
            )
        }

        setState { copy(alarmHour = hour, alarmMinute = minute) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("알람 시간을 $hour:$minute 로 변경했어요"))
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }

    private suspend fun applyAlarmMessage() {
        val message = currentState.alarmMessageBottomSheet.message
        configRepository.updateAlarmMessage(message)
        setState { copy(alarmMessage = message) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("알람 메시지를 변경했어요"))
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
    }

    companion object {
        private const val SCREEN_NAME = "Setting"
    }

    private fun clearData() = viewModelScope.launch {
        suspendRunCatching {
            todoRepository.clearData()
        }.onSuccess {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("저장된 데이터를 초기화 했어요"))
        }
    }
}
