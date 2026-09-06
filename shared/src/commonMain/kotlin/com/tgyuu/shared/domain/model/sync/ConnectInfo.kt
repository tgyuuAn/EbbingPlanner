package com.tgyuu.shared.domain.model.sync

import com.tgyuu.shared.common.now
import kotlinx.datetime.LocalDateTime

data class ConnectInfo(
    val uuid: String,
    val connectCode: String,
    val connectCodeExpirationTime: LocalDateTime,
    val deviceName: String = "",
    val connectedUuid: String? = null,
) {
    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return connectCodeExpirationTime > now
    }
}
