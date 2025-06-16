package com.tgyuu.sync.graph.connect.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Timer
import java.util.Locale

data class ConnectState(
    val uuid: String = "",
    val myCode: String = "",
    val isGenerateButtonEnabled: Boolean = true,
    val anotherCode: String = "",
    val isConnectButtonEnabled: Boolean = true,
    val remainingTimeInSec: Long = Timer.DEFAULT_DURATION_IN_SEC,
) : UiState {
    val formattedRemainingTimeInSec: String = formatTimeToMinuteSecond(remainingTimeInSec)

    private fun formatTimeToMinuteSecond(totalSeconds: Long): String {
        val minutes = totalSeconds / MINUTE_IN_SECOND
        val seconds = totalSeconds % MINUTE_IN_SECOND
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val HOUR_IN_SECOND = 3600
        private const val MINUTE_IN_SECOND = 60
    }
}
