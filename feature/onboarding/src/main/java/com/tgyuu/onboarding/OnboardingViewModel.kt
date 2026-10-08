package com.tgyuu.onboarding

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.onboarding.contract.OnboardingIntent
import com.tgyuu.onboarding.contract.OnboardingState

class OnboardingViewModel(
    private val navigationBus: NavigationBus,
) : BaseViewModel<OnboardingState, OnboardingIntent>(OnboardingState()) {

    override suspend fun processIntent(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.OnStartClick -> navigationBus.navigate(
                NavigationEvent.TopLevelTo(HomeGraph.HomeRoute())
            )
        }
    }
}
