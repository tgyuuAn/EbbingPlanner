package com.tgyuu.setting.graph.widget

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.setting.graph.widget.contract.WidgetIntent
import com.tgyuu.setting.graph.widget.contract.WidgetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<WidgetState, WidgetIntent>(WidgetState()) {

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
        currentState.selectedBackgroundAlpha ?: return@launch
        currentState.selectedTextAlpha ?: return@launch
        currentState.selectedTheme ?: return@launch

        val newBackgroundAlpha = currentState.selectedBackgroundAlpha!!
        val newTextAlpha = currentState.selectedBackgroundAlpha!!
        val newTheme = currentState.selectedTheme!!

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
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("위젯 테마를 변경하였습니다"))
        }.onFailure {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("테마 변경에 실패하였습니다"))
        }
    }
}
