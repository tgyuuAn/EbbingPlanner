package com.tgyuu.setting.graph.main.contract

sealed interface SettingSideEffect {
    data object RequestInAppReview : SettingSideEffect
    data class RequestInAppUpdate(val isImmediateUpdate: Boolean) : SettingSideEffect
}
