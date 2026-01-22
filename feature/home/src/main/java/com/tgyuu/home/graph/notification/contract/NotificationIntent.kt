package com.tgyuu.home.graph.notification.contract

import com.tgyuu.common.base.UiIntent

sealed class NotificationIntent : UiIntent {
    data object OnBackClick : NotificationIntent()
}