package com.tgyuu.shared.domain.model.sync

sealed interface ConnectResult {
    data class Success(val info: ConnectInfo) : ConnectResult

    data object InvalidOrExpired : ConnectResult

    data object AlreadyLinkedSelf : ConnectResult

    data object CodeAlreadyTaken : ConnectResult
}
