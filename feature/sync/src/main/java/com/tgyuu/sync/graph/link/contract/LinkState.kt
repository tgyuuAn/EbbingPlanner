package com.tgyuu.sync.graph.link.contract

import com.tgyuu.common.base.UiState

data class LinkState(
    val uuid: String = "",
    val code: String = "",
) : UiState {
    val isGenerateButtonEnabled = false
}
