package com.tgyuu.onboarding

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.onboarding.contract.OnboardingIntent
import com.tgyuu.onboarding.contract.OnboardingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
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
