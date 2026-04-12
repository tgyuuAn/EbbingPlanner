package com.tgyuu.shared.ui.feature.sync.connect

import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

data class ConnectState(
    val uuid: String = "",
    val myCode: String = "",
    val isGenerateButtonEnabled: Boolean = true,
    val anotherCode: String = "",
    val isConnectButtonEnabled: Boolean = true,
    val remainingTimeInSec: Long = DEFAULT_DURATION_IN_SEC,
) : UiState {
    val formattedRemainingTimeInSec: String
        get() {
            val minutes = remainingTimeInSec / 60
            val seconds = remainingTimeInSec % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }

    companion object {
        const val DEFAULT_DURATION_IN_SEC = 600L
    }
}

sealed interface ConnectIntent : UiIntent {
    data object OnBackClick : ConnectIntent
    data class OnMyCodeChange(val code: String) : ConnectIntent
    data class OnAnotherCodeChange(val code: String) : ConnectIntent
    data object OnClickGenerateCode : ConnectIntent
    data object OnClickConnectAnother : ConnectIntent
}
