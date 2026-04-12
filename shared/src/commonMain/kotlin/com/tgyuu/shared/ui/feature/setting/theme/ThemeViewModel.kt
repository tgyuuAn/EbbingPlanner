package com.tgyuu.shared.ui.feature.setting.theme

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val configRepository: ConfigRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<ThemeState, ThemeIntent>(ThemeState()) {

    init {
        loadTheme()
    }

    private fun loadTheme() {
        safeScope.launch {
            val theme = configRepository?.getAppTheme()?.first() ?: Theme.NORMAL
            setState { copy(originTheme = theme, selectTheme = theme) }
        }
    }

    override suspend fun processIntent(intent: ThemeIntent) {
        when (intent) {
            ThemeIntent.OnBackClick -> onNavigateBack()
            is ThemeIntent.OnThemeChange -> setTheme(intent.theme)
            ThemeIntent.OnUpdateClick -> updateTheme()
        }
    }

    private fun setTheme(theme: Theme) {
        setState { copy(selectTheme = theme) }
    }

    private suspend fun updateTheme() {
        val selected = currentState.selectTheme ?: return
        if (!currentState.isSaveEnabled) return

        try {
            configRepository?.setAppTheme(selected)
            setState { copy(originTheme = selected) }
            onShowSnackbar("테마가 변경되었습니다")
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar("테마 변경에 실패했습니다")
        }
    }
}
