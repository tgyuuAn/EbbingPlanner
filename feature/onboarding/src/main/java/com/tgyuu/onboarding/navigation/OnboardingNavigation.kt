package com.tgyuu.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.navigation.OnboardingRoute
import com.tgyuu.onboarding.OnboardingRoute

fun NavGraphBuilder.onboardingNavigation() {
    composable<OnboardingRoute> {
        OnboardingRoute()
    }
}
