package com.tgyuu.shared.ui.feature.setting

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingViewModel(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository? = null,
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
        private const val PRIVACY_POLICY_URL = "https://tgyuuan.notion.site/ebbing-privacy"
        private const val TERMS_OF_USE_URL = "https://tgyuuan.notion.site/ebbing-terms"
        private const val NOTICE_URL = "https://mousy-banjo-f6d.notion.site/1dd9de61e3bd80d2b4f4c70c84ad1d98"
        private const val INQUIRY_URL = "https://open.kakao.com/o/sdZLoCHh"
    }

    init {
        loadNotificationState()
        loadMondayStart()
    }

    private fun loadNotificationState() {
        viewModelScope.launch {
            val enabled = configRepository?.getNotificationEnabled()?.first() ?: true
            val (hour, minute) = configRepository?.getAlarmTime() ?: Pair(18, 30)
            val period = if (hour < 12) "오전" else "오후"
            val displayHour = if (hour % 12 == 0) 12 else hour % 12
            val timeStr = "$period ${displayHour}시 ${minute.toString().padStart(2, '0')}분"
            setState { copy(isNotificationEnabled = enabled, alarmTime = timeStr) }
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
            SettingIntent.OnInAppReviewClick -> onRequestInAppReview()
            SettingIntent.OnPrivacyPolicyClick -> onOpenUrl(PRIVACY_POLICY_URL)
            SettingIntent.OnTermsOfUseClick -> onOpenUrl(TERMS_OF_USE_URL)
            SettingIntent.OnWidgetClick -> onNavigateToWidget()
            is SettingIntent.OnUpdateStartDay -> updateStartDay(intent.mondayStart)
            SettingIntent.OnNoticeClick -> onOpenUrl(NOTICE_URL)
            SettingIntent.OnInquiryClick -> onOpenUrl(INQUIRY_URL)
        }
    }

    private suspend fun updateStartDay(mondayStart: Boolean) {
        configRepository?.setMondayStart(mondayStart)
        setState { copy(mondayStart = mondayStart) }
    }

    private suspend fun toggleNotification(enabled: Boolean) {
        configRepository?.setNotificationEnabled(enabled)
        setState { copy(isNotificationEnabled = enabled) }
    }

    private fun clearData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                todoRepository.clearData()
                onShowSnackbar("저장된 데이터를 초기화 했어요")
            } catch (e: Exception) {
                onShowSnackbar("데이터 초기화에 실패했어요")
            } finally {
                setState { copy(isLoading = false) }
            }
        }
    }
}
