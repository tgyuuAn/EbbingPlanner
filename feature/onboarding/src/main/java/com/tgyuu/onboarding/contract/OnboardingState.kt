package com.tgyuu.onboarding.contract

import com.tgyuu.common.base.UiState

data class OnboardingState(
    val isLoading: Boolean = false,
) : UiState
