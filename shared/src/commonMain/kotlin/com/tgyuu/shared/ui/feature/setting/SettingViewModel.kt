package com.tgyuu.shared.ui.feature.setting

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch

class SettingViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToTag: () -> Unit,
    private val onNavigateToRepeatCycle: () -> Unit,
    private val onNavigateToSync: () -> Unit,
    private val onNavigateToTheme: () -> Unit = {},
    private val onOpenUrl: (String) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<SettingState, SettingIntent>(SettingState()) {

    companion object {
        private const val PRIVACY_POLICY_URL = "https://tgyuuan.notion.site/ebbing-privacy"
        private const val TERMS_OF_USE_URL = "https://tgyuuan.notion.site/ebbing-terms"
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
            SettingIntent.OnPrivacyPolicyClick -> onOpenUrl(PRIVACY_POLICY_URL)
            SettingIntent.OnTermsOfUseClick -> onOpenUrl(TERMS_OF_USE_URL)
        }
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
