package com.tgyuu.sync.graph.download.contract

import com.tgyuu.common.base.UiIntent

sealed interface DownloadIntent : UiIntent {
    data object OnBackClick : DownloadIntent
    data object OnDownloadClick : DownloadIntent
}
