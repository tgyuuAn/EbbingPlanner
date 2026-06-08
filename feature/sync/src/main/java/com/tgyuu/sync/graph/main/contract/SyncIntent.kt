package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent

sealed interface SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent
    data object OnSyncUpClick : SyncIntent
    data object OnDisconnectClick : SyncIntent
    data object OnSyncDialogBackClick : SyncIntent
    data object OnSyncDialogSyncClick : SyncIntent
    data object OnDisconnectDialogBackClick : SyncIntent
    data object OnDisconnectDialogDisconnectClick : SyncIntent
    // QR 연동
    data class OnClickGenerateCode(val content: BottomSheetContent) : SyncIntent
    data object OnScanQrClick : SyncIntent
    data object OnDismissScan : SyncIntent
    data class OnQrDetected(val rawValue: String) : SyncIntent
}
