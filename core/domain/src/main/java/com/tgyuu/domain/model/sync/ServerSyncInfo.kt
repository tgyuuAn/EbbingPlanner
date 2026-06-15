package com.tgyuu.domain.model.sync

import java.time.ZonedDateTime

data class ServerSyncInfo(
    val lastUpdatedAt: ZonedDateTime?,
    val connectedDeviceName: String = "",
)
