package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed interface SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent
    data object OnSyncUpClick : SyncIntent
    data object OnConnectClick : SyncIntent
    data object OnDisconnectClick : SyncIntent
    data object OnSyncDialogBackClick : SyncIntent
    data object OnSyncDialogSyncClick : SyncIntent
    data object OnDisconnectDialogBackClick : SyncIntent
    data object OnDisconnectDialogDisconnectClick : SyncIntent
}
