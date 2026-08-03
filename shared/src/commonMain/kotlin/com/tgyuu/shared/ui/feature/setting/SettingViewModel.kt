package com.tgyuu.shared.ui.feature.setting

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.appVersionName
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ConfigRepository.Companion.DEFAULT_ALARM_MESSAGE
import com.tgyuu.shared.domain.repository.FeatureFlag
import com.tgyuu.shared.domain.repository.FeatureFlagRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.setting_announcement_url
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
    private val onNavigateToTheme: () -> Unit = {},
    private val onNavigateToNotification: () -> Unit = {},
    private val onNavigateToWidget: () -> Unit = {},
    private val onOpenUrl: (String) -> Unit = {},
    private val onRequestInAppReview: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<SettingState, SettingIntent>(SettingState()) {

    companion object {
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
            val message = configRepository?.getAlarmMessage()?.ifEmpty { DEFAULT_ALARM_MESSAGE }
                ?: DEFAULT_ALARM_MESSAGE
            setState {
                copy(
                    isNotificationEnabled = enabled,
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
            SettingIntent.OnBackClick -> onNavigateBack()
            SettingIntent.OnTagManageClick -> onNavigateToTag()
            SettingIntent.OnRepeatCycleManageClick -> onNavigateToRepeatCycle()
            SettingIntent.OnSyncClick -> onNavigateToSync()
            SettingIntent.OnClearDataClick -> { /* Show dialog from UI */ }
            SettingIntent.OnClearDataConfirm -> clearData()
            SettingIntent.OnThemeClick -> onNavigateToTheme()
            SettingIntent.OnNotificationClick -> onNavigateToNotification()
            is SettingIntent.OnNotificationToggle -> toggleNotification(intent.enabled)
            is SettingIntent.OnUpdateAlarmTime -> updateAlarmTime(intent.hour, intent.minute)
            SettingIntent.OnAlarmMessageOpen -> openAlarmMessageSheet()
            is SettingIntent.OnAlarmMessageChange -> changeAlarmMessage(intent.message)
            SettingIntent.OnAlarmMessageReset -> resetAlarmMessage()
            SettingIntent.OnApplyAlarmMessage -> applyAlarmMessage()
            SettingIntent.OnInAppReviewClick -> onRequestInAppReview()
            // 문서 URL은 로케일별 공개 게시(notion.site) 링크로 분기
            SettingIntent.OnPrivacyPolicyClick ->
                onOpenUrl(getString(Res.string.setting_privacy_policy_url))
            SettingIntent.OnTermsOfUseClick ->
                onOpenUrl(getString(Res.string.setting_term_url))
            SettingIntent.OnWidgetClick -> onNavigateToWidget()
            is SettingIntent.OnUpdateStartDay -> updateStartDay(intent.mondayStart)
            SettingIntent.OnAutoBackupToggleClick -> onAutoBackupToggleClick()
            SettingIntent.OnNoticeClick ->
                onOpenUrl(getString(Res.string.setting_announcement_url))
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
        configRepository?.setNotificationEnabled(enabled)
        setState { copy(isNotificationEnabled = enabled) }
    }

    private suspend fun updateAlarmTime(hour: Int, minute: Int) {
        configRepository?.updateAlarmTime(hour.toString(), minute.toString())
        setState {
            copy(
                alarmHour = hour,
                alarmMinute = minute,
            )
        }
        onShowSnackbar(getString(Res.string.snack_alarm_time_changed))
    }

    private fun openAlarmMessageSheet() {
        val current = currentState.alarmMessage.ifEmpty { DEFAULT_ALARM_MESSAGE }
        setState {
            copy(
                alarmMessageBottomSheet = AlarmMessageBottomSheetState(
                    message = current,
                    originMessage = current,
                ),
            )
        }
    }

    private fun changeAlarmMessage(message: String) {
        setState { copy(alarmMessageBottomSheet = alarmMessageBottomSheet.copy(message = message)) }
    }

    private fun resetAlarmMessage() {
        setState {
            copy(alarmMessageBottomSheet = alarmMessageBottomSheet.copy(message = DEFAULT_ALARM_MESSAGE))
        }
    }

    private suspend fun applyAlarmMessage() {
        val message = currentState.alarmMessageBottomSheet.message
        configRepository?.updateAlarmMessage(message)
        setState { copy(alarmMessage = message) }
        onShowSnackbar(getString(Res.string.snack_alarm_message_changed))
    }

    private fun clearData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                todoRepository.clearData()
                onShowSnackbar(getString(Res.string.snack_data_cleared))
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_data_clear_failed))
            } finally {
                setState { copy(isLoading = false) }
            }
        }
    }
}
