package com.tgyuu.sync.graph.connect.contract

import com.tgyuu.common.base.UiIntent

sealed interface ConnectIntent : UiIntent {
    data object OnBackClick : ConnectIntent
    data class OnMyCodeChange(val code: String) : ConnectIntent
    data class OnAnotherCodeChange(val code: String) : ConnectIntent
    data object OnClickGenerateCode: ConnectIntent
    data object OnClickConnectAnother: ConnectIntent
}
