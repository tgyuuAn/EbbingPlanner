package com.tgyuu.sync.graph.link.contract

import com.tgyuu.common.base.UiIntent

sealed interface LinkIntent : UiIntent {
    data object OnBackClick : LinkIntent
    data class OnMyCodeChange(val code: String) : LinkIntent
    data class OnAnotherCodeChange(val code: String) : LinkIntent
    data object OnClickGenerateCode: LinkIntent
    data object OnClickConnectAnother: LinkIntent
}
