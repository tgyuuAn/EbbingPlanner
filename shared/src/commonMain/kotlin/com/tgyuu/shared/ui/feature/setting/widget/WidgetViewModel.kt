package com.tgyuu.shared.ui.feature.setting.widget

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WidgetViewModel(
    private val configRepository: ConfigRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
) : BaseViewModel<WidgetState, WidgetIntent>(WidgetState()) {

    init {
        loadExperimentVariant()
        loadWidgetConfig()
    }

    private fun loadWidgetConfig() {
        safeScope.launch {
            val theme = configRepository?.getWidgetTheme()?.first() ?: Theme.NORMAL
            val bgAlpha = configRepository?.getWidgetBackgroundAlpha()?.first() ?: 1.0f
            val textAlpha = configRepository?.getWidgetTextAlpha()?.first() ?: 1.0f
            setState {
                copy(
                    originTheme = theme, selectedTheme = theme,
                    originBackgroundAlpha = bgAlpha, selectedBackgroundAlpha = bgAlpha,
                    originTextAlpha = textAlpha, selectedTextAlpha = textAlpha,
                )
            }
        }
    }

    override suspend fun processIntent(intent: WidgetIntent) {
        when (intent) {
            WidgetIntent.OnBackClick -> onNavigateBack()
            is WidgetIntent.OnThemeChange -> setState { copy(selectedTheme = intent.theme) }
            is WidgetIntent.OnBackgroundAlphaChange -> setState { copy(selectedBackgroundAlpha = intent.alpha) }
            is WidgetIntent.OnTextAlphaChange -> setState { copy(selectedTextAlpha = intent.alpha) }
            WidgetIntent.OnSaveClick -> saveWidgetConfig()
        }
    }

    private suspend fun saveWidgetConfig() {
        if (!currentState.isSaveEnabled) return
        try {
            currentState.selectedTheme?.let { configRepository?.setWidgetTheme(it) }
            currentState.selectedBackgroundAlpha?.let { configRepository?.setWidgetBackgroundAlpha(it) }
            currentState.selectedTextAlpha?.let { configRepository?.setWidgetTextAlpha(it) }
            setState {
                copy(
                    originTheme = selectedTheme,
                    originBackgroundAlpha = selectedBackgroundAlpha,
                    originTextAlpha = selectedTextAlpha,
                )
            }
            onShowSnackbar("위젯 설정이 저장되었습니다")
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar("위젯 설정 저장에 실패했습니다")
        }
    }

    private fun loadExperimentVariant() {
        safeScope.launch {
            val variant = experimentRepository?.getVariant(Experiment.SaveButtonPosition)
                ?: Experiment.SaveButtonPosition.Variant.CONTROL
            setState { copy(saveButtonPositionVariant = variant) }
        }
    }
}
