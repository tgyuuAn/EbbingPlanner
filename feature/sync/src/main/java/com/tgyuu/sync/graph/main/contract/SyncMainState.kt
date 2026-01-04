package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiState
import kotlinx.datetime.LocalDateTime

data class SyncMainState(
    val uuid: String = "",
    val linkedUuid: String? = null,
    val localLastSyncedAt: LocalDateTime? = null,
    val serverLastUpdatedAt: LocalDateTime? = null,
    val isNetworkLoading: Boolean = true,
    val isSyncUpEnabled: Boolean = true,
) : UiState
