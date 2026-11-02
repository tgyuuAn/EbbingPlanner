package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.Timer
import java.time.ZonedDateTime
import java.util.Locale

data class SyncMainState(
    val uuid: String = "",
    val deviceName: String = "",
    val linkedUuid: String? = null,
    val connectedDeviceName: String? = null,
    val localLastSyncedAt: ZonedDateTime? = null,
    val serverLastUpdatedAt: ZonedDateTime? = null,
    val isNetworkLoading: Boolean = true,
    val isInitialLoading: Boolean = true,
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

    val displayDeviceInfo: String
        get() = if (deviceName.isNotEmpty() && uuid.length >= 8)
            "$deviceName · ${uuid.take(8)}" else uuid

    val displayLinkedDeviceInfo: String?
        get() = linkedUuid?.let { uid ->
            val name = connectedDeviceName
            if (!name.isNullOrEmpty() && uid.length >= 8) "$name · ${uid.take(8)}" else uid
        }

    val connectedDeviceUuidPrefix: String
        get() = linkedUuid?.take(8) ?: ""

    val connectedDeviceEmoji: String
        get() {
            val name = connectedDeviceName ?: ""
            val isTablet = name.contains("Tab", ignoreCase = true) ||
                    name.contains("Pad", ignoreCase = true)
            return if (isTablet) "💻" else "📱"
        }
}
