package com.tgyuu.shared.ui.feature.setting

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.platform.NotificationPermissionStatus
import com.tgyuu.shared.platform.NotificationScheduler
import com.tgyuu.shared.platform.openAppSettings
import com.tgyuu.shared.common.appVersionName
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.alarm_placeholder_token
import ebbingplanner.shared.generated.resources.default_alarm_message
import ebbingplanner.shared.generated.resources.setting_announcement
import ebbingplanner.shared.generated.resources.setting_announcement_url
import ebbingplanner.shared.generated.resources.setting_privacy_policy
import ebbingplanner.shared.generated.resources.setting_term
import ebbingplanner.shared.generated.resources.setting_privacy_policy_url
import ebbingplanner.shared.generated.resources.setting_term_url
import ebbingplanner.shared.generated.resources.snack_alarm_message_changed
import ebbingplanner.shared.generated.resources.snack_alarm_time_changed
import ebbingplanner.shared.generated.resources.snack_data_clear_failed
import ebbingplanner.shared.generated.resources.snack_data_cleared
import org.jetbrains.compose.resources.getString

class SettingViewModel(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository? = null,
    private val featureFlagRepository: FeatureFlagRepository? = null,
    private val syncRepository: SyncRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToTag: () -> Unit,
    private val onNavigateToRepeatCycle: () -> Unit,
    private val onNavigateToSync: () -> Unit,
    private val onNavigateToRestore: () -> Unit = {},
    private val onNavigateToTheme: () -> Unit = {},
    private val onNavigateToWidget: () -> Unit = {},
    private val onOpenUrl: (String) -> Unit = {},
    /** Android SettingViewModel.navigateToWebView 대응: 제목을 명시해 웹뷰로 이동 */
    private val onNavigateToWebView: (title: String, url: String) -> Unit = { _, url -> onOpenUrl(url) },
    private val onRequestInAppReview: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val analyticsHelper: com.tgyuu.shared.platform.AnalyticsHelper? = null,
    private val notificationScheduler: NotificationScheduler? = null,
    private val alarmRescheduler: AlarmRescheduler? = null,
) : BaseViewModel<SettingState, SettingIntent>(SettingState()) {

    // Android NotificationBody와 동일: 토글 표시값 = 저장된 설정(enabled) && OS 알림 권한 허용
    private var notificationConfigEnabled = true
    private var notificationPermissionGranted = true
    // 권한 거부 상태에서 토글 ON → 앱 설정으로 보낸 뒤, 복귀 시 권한이 허용되었으면 자동으로 켠다
    private var pendingEnableAfterSettings = false

    companion object {
        private const val SCREEN_NAME = "Setting"
        private const val INQUIRY_URL = "https://open.kakao.com/o/sdZLoCHh"
    }

    init {
        val version = appVersionName()
        if (version.isNotEmpty()) {
            setState { copy(appVersion = version) }
        }
        loadNotificationState()
        loadMondayStart()
        loadAutoBackup()
    }

    private fun loadAutoBackup() {
        viewModelScope.launch {
            featureFlagRepository?.fetchAndAwait()
            val featureEnabled = featureFlagRepository?.getBoolean(FeatureFlag.USE_AUTO_BACKUP) ?: false
            setState { copy(autoBackupFeatureEnabled = featureEnabled) }

            if (featureEnabled) {
                val lastSync = runCatching { syncRepository?.getLocalSyncedAt() }.getOrNull()
                setState { copy(lastSyncTime = lastSync?.toFormattedString()) }
                configRepository?.getAutoBackupEnabled()
                    ?.collect { setState { copy(autoBackupEnabled = it) } }
            }
        }
    }

    private fun loadNotificationState() {
        viewModelScope.launch {
            val enabled = configRepository?.getNotificationEnabled()?.first() ?: true
            val (hour, minute) = configRepository?.getAlarmTime() ?: Pair(18, 30)
            // Android SettingViewModel과 동일: 저장 sentinel은 로케일별 기본 문구로 표시
            val message = resolveDisplayMessage(configRepository?.getAlarmMessage().orEmpty())
            notificationConfigEnabled = enabled
            notificationPermissionGranted = notificationScheduler
                ?.getPermissionStatus()
                ?.let { it == NotificationPermissionStatus.GRANTED }
                ?: true
            setState {
                copy(
                    isNotificationEnabled = notificationConfigEnabled && notificationPermissionGranted,
                    alarmHour = hour,
                    alarmMinute = minute,
                    alarmMessage = message,
                )
            }
        }
    }

    private fun loadMondayStart() {
        viewModelScope.launch {
            configRepository?.getMondayStart()
                ?.collect { setState { copy(mondayStart = it) } }
        }
    }

    override suspend fun processIntent(intent: SettingIntent) {
        when (intent) {
            SettingIntent.OnBackClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "Back")
                onNavigateBack()
            }
            SettingIntent.OnTagManageClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "ManageTag")
                onNavigateToTag()
            }
            SettingIntent.OnRepeatCycleManageClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "ManageRepeatCycle")
                onNavigateToRepeatCycle()
            }
            SettingIntent.OnSyncClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "SyncData")
                onNavigateToSync()
            }
            SettingIntent.OnRestoreByDeviceIdClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "RestoreByDeviceId")
                onNavigateToRestore()
            }
            SettingIntent.OnClearDataClick -> analyticsHelper.logClick(SCREEN_NAME, "ClearData")
            SettingIntent.OnClearDataConfirm -> clearData()
            SettingIntent.OnThemeClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "EditAppTheme")
                onNavigateToTheme()
            }
            SettingIntent.OnResume -> refreshNotificationPermission()
            is SettingIntent.OnNotificationToggle -> {
                analyticsHelper.logClick(SCREEN_NAME, "NotificationNudge_Toggle")
                toggleNotification(intent.enabled)
            }
            is SettingIntent.OnUpdateAlarmTime -> {
                analyticsHelper.logClick(SCREEN_NAME, "NotificationNudge_Time")
                updateAlarmTime(intent.hour, intent.minute)
            }
            SettingIntent.OnAlarmMessageOpen -> {
                analyticsHelper.logClick(SCREEN_NAME, "NotificationMessage")
                openAlarmMessageSheet()
            }
            is SettingIntent.OnAlarmMessageChange -> changeAlarmMessage(intent.message)
            SettingIntent.OnAlarmMessageReset -> resetAlarmMessage()
            SettingIntent.OnApplyAlarmMessage -> applyAlarmMessage()
            SettingIntent.OnInAppReviewClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "InAppReview")
                onRequestInAppReview()
            }
            // 문서 URL은 로케일별 공개 게시(notion.site) 링크로 분기
            SettingIntent.OnPrivacyPolicyClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "PrivacyAndPolicy")
                onNavigateToWebView(getString(Res.string.setting_privacy_policy), getString(Res.string.setting_privacy_policy_url))
            }
            SettingIntent.OnTermsOfUseClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "TermsOfUse")
                onNavigateToWebView(getString(Res.string.setting_term), getString(Res.string.setting_term_url))
            }
            SettingIntent.OnWidgetClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "EditWidgetTheme")
                onNavigateToWidget()
            }
            is SettingIntent.OnUpdateStartDay -> {
                analyticsHelper.logClick(SCREEN_NAME, "ApplyStartDay")
                updateStartDay(intent.mondayStart)
            }
            SettingIntent.OnAutoBackupToggleClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "AutoBackup_Toggle")
                onAutoBackupToggleClick()
            }
            SettingIntent.OnNoticeClick -> {
                analyticsHelper.logClick(SCREEN_NAME, "Notice")
                onNavigateToWebView(getString(Res.string.setting_announcement), getString(Res.string.setting_announcement_url))
            }
            SettingIntent.OnInquiryClick -> onOpenUrl(INQUIRY_URL)
        }
    }

    private suspend fun updateStartDay(mondayStart: Boolean) {
        configRepository?.setMondayStart(mondayStart)
        setState { copy(mondayStart = mondayStart) }
    }

    private suspend fun onAutoBackupToggleClick() {
        configRepository?.setAutoBackupEnabled(!currentState.autoBackupEnabled)
    }

    private suspend fun toggleNotification(enabled: Boolean) {
        val scheduler = notificationScheduler
        if (enabled && scheduler != null) {
            // Android handlePermission 대응: 최초 요청이면 시스템 권한 요청, 이미 거부했으면 앱 설정으로 이동
            when (scheduler.getPermissionStatus()) {
                NotificationPermissionStatus.GRANTED -> notificationPermissionGranted = true
                NotificationPermissionStatus.NOT_DETERMINED -> {
                    notificationPermissionGranted = scheduler.requestPermission()
                    if (!notificationPermissionGranted) {
                        publishNotificationEnabled()
                        return
                    }
                }
                NotificationPermissionStatus.DENIED -> {
                    pendingEnableAfterSettings = true
                    openAppSettings()
                    return
                }
            }
        }
        pendingEnableAfterSettings = false
        configRepository?.setNotificationEnabled(enabled)
        notificationConfigEnabled = enabled
        // ON이면 전체 재계산, OFF면 전부 취소 (rescheduleAll이 꺼진 설정을 보고 전체 취소)
        if (enabled) alarmRescheduler?.rescheduleAll() else alarmRescheduler?.cancelAll()
        publishNotificationEnabled()
    }

    /** 화면 복귀(앱 설정에서 돌아온 경우 포함) 시 OS 알림 권한을 다시 읽어 토글 표시를 갱신한다. */
    private suspend fun refreshNotificationPermission() {
        val scheduler = notificationScheduler ?: return
        notificationPermissionGranted = scheduler.getPermissionStatus() == NotificationPermissionStatus.GRANTED
        if (pendingEnableAfterSettings && notificationPermissionGranted) {
            pendingEnableAfterSettings = false
            configRepository?.setNotificationEnabled(true)
            notificationConfigEnabled = true
            alarmRescheduler?.rescheduleAll()
        }
        publishNotificationEnabled()
    }

    private fun publishNotificationEnabled() {
        setState { copy(isNotificationEnabled = notificationConfigEnabled && notificationPermissionGranted) }
    }

    private suspend fun updateAlarmTime(hour: Int, minute: Int) {
        configRepository?.updateAlarmTime(hour.toString(), minute.toString())
        alarmRescheduler?.rescheduleAll()
        setState {
            copy(
                alarmHour = hour,
                alarmMinute = minute,
            )
        }
        onShowSnackbar(getString(Res.string.snack_alarm_time_changed))
    }

    private suspend fun resolveDisplayMessage(stored: String): String =
        if (stored.isEmpty() || stored == DEFAULT_ALARM_MESSAGE) getString(Res.string.default_alarm_message)
        else stored

    private suspend fun openAlarmMessageSheet() {
        val defaultMessage = getString(Res.string.default_alarm_message)
        val placeholderToken = getString(Res.string.alarm_placeholder_token)
        val current = resolveDisplayMessage(currentState.alarmMessage)
        setState {
            copy(
                alarmMessageBottomSheet = AlarmMessageBottomSheetState(
                    defaultMessage = defaultMessage,
                    message = current,
                    originMessage = current,
                    placeholderToken = placeholderToken,
                ),
            )
        }
    }

    private fun changeAlarmMessage(message: String) {
        setState { copy(alarmMessageBottomSheet = alarmMessageBottomSheet.copy(message = message)) }
    }

    private fun resetAlarmMessage() {
        setState {
            copy(alarmMessageBottomSheet = alarmMessageBottomSheet.copy(message = alarmMessageBottomSheet.defaultMessage))
        }
    }

    private suspend fun applyAlarmMessage() {
        val message = currentState.alarmMessageBottomSheet.message
        configRepository?.updateAlarmMessage(message)
        setState { copy(alarmMessage = message) }
        alarmRescheduler?.rescheduleAll()
        onShowSnackbar(getString(Res.string.snack_alarm_message_changed))
    }

    private fun clearData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                todoRepository.clearData()
                alarmRescheduler?.cancelAll()
                onShowSnackbar(getString(Res.string.snack_data_cleared))
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_data_clear_failed))
            } finally {
                setState { copy(isLoading = false) }
            }
        }
    }
}
