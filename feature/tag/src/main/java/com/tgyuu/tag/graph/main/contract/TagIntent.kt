package com.tgyuu.tag.graph.main.contract

import com.tgyuu.common.base.UiIntent

sealed class TagIntent : UiIntent {
    data object OnBackClick : TagIntent()
}
