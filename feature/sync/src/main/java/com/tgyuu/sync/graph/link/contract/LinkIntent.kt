package com.tgyuu.sync.graph.link.contract

import com.tgyuu.common.base.UiIntent

sealed interface LinkIntent : UiIntent {
    data object OnBackClick : LinkIntent
    data object OnLinkClick : LinkIntent
}
