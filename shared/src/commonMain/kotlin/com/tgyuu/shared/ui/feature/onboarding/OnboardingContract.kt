package com.tgyuu.shared.ui.feature.onboarding

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

@Immutable
data class OnboardingState(
    val isLoading: Boolean = false,
) : UiState

sealed class OnboardingIntent : UiIntent {
    data object OnStartClick : OnboardingIntent()
}
