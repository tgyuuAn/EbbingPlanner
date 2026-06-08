package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Timer
import java.time.ZonedDateTime
import java.util.Locale

data class SyncMainState(
    val uuid: String = "",
    val linkedUuid: String? = null,
    val localLastSyncedAt: ZonedDateTime? = null,
    val serverLastUpdatedAt: ZonedDateTime? = null,
    val isNetworkLoading: Boolean = true,
    val isSyncUpEnabled: Boolean = true,
    // QR 연동
    val connectCode: String = "",
    val isGenerateButtonEnabled: Boolean = true,
    val remainingTimeInSec: Long = Timer.DEFAULT_DURATION_IN_SEC,
    val isScanning: Boolean = false,
    val isScanLoading: Boolean = false,
) : UiState {
    val qrContent: String get() = "ebbingplanner://$connectCode"
    val formattedRemainingTimeInSec: String get() {
        val minutes = remainingTimeInSec / 60
        val seconds = remainingTimeInSec % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
