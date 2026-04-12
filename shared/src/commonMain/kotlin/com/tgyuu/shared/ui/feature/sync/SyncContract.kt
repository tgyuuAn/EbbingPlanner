package com.tgyuu.shared.ui.feature.sync

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState
import kotlinx.datetime.LocalDateTime

@Immutable
data class SyncState(
    val uuid: String = "",
    val linkedUuid: String? = null,
    val localLastSyncedAt: LocalDateTime? = null,
    val serverLastUpdatedAt: LocalDateTime? = null,
    val isNetworkLoading: Boolean = true,
    val isSyncUpEnabled: Boolean = true,
) : UiState {
    val isLinked: Boolean
        get() = !linkedUuid.isNullOrEmpty()
}

sealed class SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent()
    data object OnSyncUpClick : SyncIntent()
    data object OnConnectClick : SyncIntent()
    data object OnDisconnectClick : SyncIntent()
}
