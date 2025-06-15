package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed interface SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent
    data object OnSyncUpClick : SyncIntent
    data object OnLinkClick : SyncIntent
}
