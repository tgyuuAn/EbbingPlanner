package com.tgyuu.setting.graph.theme

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
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
) : BaseViewModel<ThemeState, ThemeIntent>(ThemeState()) {

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
            ThemeIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            ThemeIntent.OnUpdateClick -> updateTheme()
        }
    }

    private fun setTheme(new: Theme) {
        setState { copy(selectTheme = new) }
    }

    private fun updateTheme() {
        viewModelScope.launch {
            currentState.selectTheme?.let { select ->
                suspendRunCatching {
                    configRepository.setAppTheme(select)
                }.onSuccess {
                    setState { copy(originTheme = select) }
                    eventBus.sendEvent(EbbingEvent.ShowSnackBar("테마를 변경하였습니다"))
                }
            }
        }
    }
}
