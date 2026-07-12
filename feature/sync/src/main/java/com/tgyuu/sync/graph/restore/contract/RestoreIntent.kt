package com.tgyuu.sync.graph.restore.contract

import com.tgyuu.common.base.UiIntent

sealed class RestoreIntent : UiIntent {
    data object OnBackClick : RestoreIntent()
    data class OnDeviceIdChange(val deviceId: String) : RestoreIntent()
    data object OnRestoreClick : RestoreIntent()
}
