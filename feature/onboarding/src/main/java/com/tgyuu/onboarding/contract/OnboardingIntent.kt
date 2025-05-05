package com.tgyuu.onboarding.contract

import com.tgyuu.common.base.UiIntent

sealed interface OnboardingIntent : UiIntent {
    data object OnStartClick : OnboardingIntent
}
