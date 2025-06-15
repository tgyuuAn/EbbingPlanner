package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiState
import java.time.Duration
import java.time.ZonedDateTime

data class SyncMainState(
    val uuid: String = "",
    val linkedUuid: String? = null,
    val localLastSyncedAt: ZonedDateTime? = null,
    val serverLastUpdatedAt: ZonedDateTime? = null,
    val isNetworkLoading: Boolean = true,
) : UiState {
    val isSyncUpEnabled: Boolean
        get() = serverLastUpdatedAt == null ||
                Duration.between(serverLastUpdatedAt, ZonedDateTime.now()).toMinutes() >= 10
}
