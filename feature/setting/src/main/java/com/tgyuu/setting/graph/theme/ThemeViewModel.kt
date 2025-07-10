package com.tgyuu.setting.graph.theme

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.setting.graph.theme.contract.ThemeIntent
import com.tgyuu.setting.graph.theme.contract.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<ThemeState, ThemeIntent>(ThemeState()) {

    internal suspend fun loadTheme() {
        val origin = configRepository.getTheme().first()
        setState {
            copy(
                originTheme = origin,
                selectTheme = origin,
            )
        }
    }

    override suspend fun processIntent(intent: ThemeIntent) {
        when (intent) {
            is ThemeIntent.OnThemeChange -> Unit
            ThemeIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            ThemeIntent.OnUpdateClick -> Unit
        }
    }
}
