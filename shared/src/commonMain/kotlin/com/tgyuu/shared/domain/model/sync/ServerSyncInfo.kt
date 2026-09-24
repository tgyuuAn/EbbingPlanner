package com.tgyuu.shared.domain.model.sync

import kotlinx.datetime.LocalDateTime

data class ServerSyncInfo(
    val lastUpdatedAt: LocalDateTime?,
    val connectedDeviceName: String = "",
)
