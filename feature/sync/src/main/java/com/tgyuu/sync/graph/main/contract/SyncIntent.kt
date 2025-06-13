package com.tgyuu.sync.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed interface SyncIntent : UiIntent {
    data object OnBackClick : SyncIntent
    data object OnUuidClick : SyncIntent
    data object OnUploadClick : SyncIntent
    data object OnDownloadClick : SyncIntent
    data object OnLinkClick : SyncIntent
}
