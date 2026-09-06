package com.tgyuu.shared.ui.feature.onboarding

import com.tgyuu.shared.base.BaseViewModel

class OnboardingViewModel(
    private val onNavigateToHome: () -> Unit,
) : BaseViewModel<OnboardingState, OnboardingIntent>(OnboardingState()) {

    override suspend fun processIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.OnStartClick -> onNavigateToHome()
        }
    }
}
