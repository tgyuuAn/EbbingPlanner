package com.tgyuu.sync.graph.restore.contract

import androidx.compose.runtime.Immutable
import com.tgyuu.common.base.UiState

@Immutable
data class RestoreState(
    val deviceId: String = "",
    val isRestoring: Boolean = false,
) : UiState {
    val isRestoreEnabled: Boolean
        get() = deviceId.isNotBlank() && !isRestoring
}
