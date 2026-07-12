package com.tgyuu.shared.ui.feature.sync.restore

import androidx.compose.runtime.Immutable
import com.tgyuu.shared.base.UiIntent
import com.tgyuu.shared.base.UiState

@Immutable
data class RestoreState(
    val deviceId: String = "",
    val isRestoring: Boolean = false,
) : UiState {
    val isRestoreEnabled: Boolean
        get() = deviceId.isNotBlank() && !isRestoring
}

sealed class RestoreIntent : UiIntent {
    data object OnBackClick : RestoreIntent()
    data class OnDeviceIdChange(val deviceId: String) : RestoreIntent()
    data object OnRestoreClick : RestoreIntent()
}
