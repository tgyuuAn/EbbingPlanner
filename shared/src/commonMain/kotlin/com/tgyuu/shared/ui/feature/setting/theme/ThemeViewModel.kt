package com.tgyuu.shared.ui.feature.setting.theme

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.Theme
import com.tgyuu.shared.domain.repository.ConfigRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_theme_change_failed
import ebbingplanner.shared.generated.resources.snack_theme_changed
import org.jetbrains.compose.resources.getString

class ThemeViewModel(
    private val configRepository: ConfigRepository? = null,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
) : BaseViewModel<ThemeState, ThemeIntent>(ThemeState()) {

    init {
        loadExperimentVariant()
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
            onShowSnackbar(getString(Res.string.snack_theme_changed))
            onNavigateBack()
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_theme_change_failed))
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
