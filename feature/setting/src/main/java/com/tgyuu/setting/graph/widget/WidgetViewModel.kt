package com.tgyuu.setting.graph.widget

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.setting.graph.widget.contract.WidgetIntent
import com.tgyuu.setting.graph.widget.contract.WidgetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<WidgetState, WidgetIntent>(WidgetState()) {

    override suspend fun processIntent(intent: WidgetIntent) {
        when (intent) {
            WidgetIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            WidgetIntent.OnSaveClick -> Unit
            is WidgetIntent.OnAlphaChange -> setAlpha(intent.alpha)
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

    internal suspend fun loadWidgetAlpha() {
        val origin = configRepository.getWidgetAlpha().first()
        setState {
            copy(
                originAlpha = origin,
                selectedAlpha = origin,
            )
        }
    }

    private fun setAlpha(alpha: Float) {
        setState { copy(selectedAlpha = alpha) }
    }

    private fun setTheme(theme: Theme) {
        setState { copy(selectedTheme = theme) }
    }
}
