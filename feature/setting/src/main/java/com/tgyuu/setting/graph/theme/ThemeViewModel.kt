package com.tgyuu.setting.graph.theme

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
import com.tgyuu.setting.graph.theme.contract.ThemeIntent
import com.tgyuu.setting.graph.theme.contract.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<ThemeState, ThemeIntent>(
    ThemeState()
) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "Theme",
            )
        )
    }

    internal suspend fun loadTheme() {
        val origin = configRepository.getAppTheme().first()
        setState {
            copy(
                originTheme = origin,
                selectTheme = origin,
            )
        }
    }

    override suspend fun processIntent(intent: ThemeIntent) {
        when (intent) {
            is ThemeIntent.OnThemeChange -> setTheme(intent.theme)
            ThemeIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "Theme", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            ThemeIntent.OnUpdateClick -> updateTheme()
        }
    }

    private fun setTheme(new: Theme) {
        setState { copy(selectTheme = new) }
    }

    private fun updateTheme() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "Theme",
                buttonName = "Apply",
            )
        )

        viewModelScope.launch {
            currentState.selectTheme?.let { select ->
                suspendRunCatching {
                    configRepository.setAppTheme(select)
                }.onSuccess {
                    setState { copy(originTheme = select) }
                    navigationBus.navigate(NavigationEvent.Up)
                    eventBus.sendEvent(
                        EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.setting_app_theme_changed))
                    )
                }.onFailure {
                    eventBus.sendEvent(
                        EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.setting_theme_change_failed))
                    )
                }
            }
        }
    }
}
