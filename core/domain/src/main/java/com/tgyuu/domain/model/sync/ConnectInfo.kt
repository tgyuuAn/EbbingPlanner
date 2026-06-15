package com.tgyuu.domain.model.sync

import java.time.LocalDateTime

data class ConnectInfo(
    val uuid: String,
    val connectCode: String,
    val connectCodeExpirationTime: LocalDateTime,
    val deviceName: String = "",
    val connectedUuid: String? = null,
) {
    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return connectCodeExpirationTime.isAfter(now)
    }
}
