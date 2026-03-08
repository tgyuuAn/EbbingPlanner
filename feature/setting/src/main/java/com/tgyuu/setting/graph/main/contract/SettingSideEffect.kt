package com.tgyuu.setting.graph.main.contract

sealed interface SettingSideEffect {
    data object RequestInAppReview : SettingSideEffect
}
