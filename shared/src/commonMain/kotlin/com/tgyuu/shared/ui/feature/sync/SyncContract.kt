package com.tgyuu.shared.ui.feature.sync

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import com.tgyuu.shared.domain.model.Timer
import kotlinx.datetime.LocalDateTime

const val EBBINGPLANNER_SCHEME = "ebbingplanner://"

@Immutable
data class SyncState(
    val uuid: String = "",
    val deviceName: String = "",
    val linkedUuid: String? = null,
    val connectedDeviceName: String? = null,
    val localLastSyncedAt: LocalDateTime? = null,
    val serverLastUpdatedAt: LocalDateTime? = null,
    val isInitialLoading: Boolean = true,
    val isNetworkLoading: Boolean = false,
    val isSyncUpEnabled: Boolean = true,
    // QR 연동
    val connectCode: String = "",
    val isGenerateButtonEnabled: Boolean = true,
    val remainingTimeInSec: Long = Timer.DEFAULT_DURATION_IN_SEC,
    val isQrSheetVisible: Boolean = false,
    val isScanning: Boolean = false,
    val isScanLoading: Boolean = false,
) : UiState {
    val qrContent: String get() = "$EBBINGPLANNER_SCHEME$connectCode"

    val formattedRemainingTimeInSec: String
        get() {
            val remaining = remainingTimeInSec.coerceAtLeast(0L)
            val minutes = (remaining / 60).toString().padStart(2, '0')
            val seconds = (remaining % 60).toString().padStart(2, '0')
            return "$minutes:$seconds"
        }

    val displayDeviceInfo: String
        get() = if (deviceName.isNotEmpty() && uuid.length >= 8)
            "$deviceName · ${uuid.take(8)}" else uuid

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

sealed class SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent()
    data object OnSyncUpClick : SyncIntent()
    data object OnSyncUpDisabledClick : SyncIntent()
    data object OnDisconnectClick : SyncIntent()
    data object OnRestoreClick : SyncIntent()
    data object OnDeviceInfoCopied : SyncIntent()

    // QR 연동
    data object OnGenerateQrClick : SyncIntent()
    data object OnDismissQrSheet : SyncIntent()
    data object OnScanQrClick : SyncIntent()
    data object OnDismissScan : SyncIntent()
    data class OnQrDetected(val rawValue: String) : SyncIntent()
}
