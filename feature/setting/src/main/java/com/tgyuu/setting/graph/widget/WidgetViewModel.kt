package com.tgyuu.setting.graph.widget

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.setting.graph.widget.contract.WidgetIntent
import com.tgyuu.setting.graph.widget.contract.WidgetState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WidgetViewModel(
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<WidgetState, WidgetIntent>(
    WidgetState()
) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "Widget",
            )
        )
    }

    override suspend fun processIntent(intent: WidgetIntent) {
        when (intent) {
            WidgetIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            WidgetIntent.OnSaveClick -> saveWidgetConfigure()
            is WidgetIntent.OnBackgroundAlphaChange -> setBackgroundAlpha(intent.alpha)
            is WidgetIntent.OnTextAlphaChange -> setTextAlpha(intent.alpha)
            is WidgetIntent.OnThemeChange -> setTheme(intent.theme)
        }
    }

    internal suspend fun loadWidgetTheme() {
        val origin = configRepository.getWidgetTheme().first()
        setState {
            copy(
                originTheme = origin,
                selectedTheme = origin,
            )
        }
    }

    internal suspend fun loadWidgetBackgroundAlpha() {
        val origin = configRepository.getWidgetBackgroundAlpha().first()
        setState {
            copy(
                originBackgroundAlpha = origin,
                selectedBackgroundAlpha = origin,
            )
        }
    }

    internal suspend fun loadWidgetTextAlpha() {
        val origin = configRepository.getWidgetTextAlpha().first()
        setState {
            copy(
                originTextAlpha = origin,
                selectedTextAlpha = origin,
            )
        }
    }

    private fun setBackgroundAlpha(alpha: Float) {
        setState { copy(selectedBackgroundAlpha = alpha) }
    }

    private fun setTextAlpha(alpha: Float) {
        setState { copy(selectedTextAlpha = alpha) }
    }

    private fun setTheme(theme: Theme) {
        setState { copy(selectedTheme = theme) }
    }

    private fun saveWidgetConfigure() = viewModelScope.launch {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "Widget",
                buttonName = "Apply",
            )
        )

        val newBackgroundAlpha = currentState.selectedBackgroundAlpha ?: return@launch
        val newTextAlpha = currentState.selectedTextAlpha ?: return@launch
        val newTheme = currentState.selectedTheme ?: return@launch

        suspendRunCatching {
            val backgroundAlphaJob =
                launch { configRepository.setWidgetBackgroundAlpha(newBackgroundAlpha) }
            val textAlphaJob = launch { configRepository.setWidgetTextAlpha(newTextAlpha) }
            val themeJob = launch { configRepository.setWidgetTheme(newTheme) }

            backgroundAlphaJob.join()
            textAlphaJob.join()
            themeJob.join()
        }.onSuccess {
            setState {
                copy(
                    originTheme = newTheme,
                    originBackgroundAlpha = newBackgroundAlpha,
                    originTextAlpha = newTextAlpha,
                )
            }
            navigationBus.navigate(NavigationEvent.Up)
            eventBus.sendEvent(
                EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.setting_widget_theme_changed))
            )
        }.onFailure {
            eventBus.sendEvent(
                EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.setting_theme_change_failed))
            )
        }
    }
}
