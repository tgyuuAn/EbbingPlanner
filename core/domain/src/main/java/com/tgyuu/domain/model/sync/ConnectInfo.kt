package com.tgyuu.domain.model.sync

import com.tgyuu.common.now
import kotlinx.datetime.LocalDateTime

data class ConnectInfo(
    val uuid: String,
    val connectCode: String,
    val connectCodeExpirationTime: LocalDateTime,
) {
    fun isValid(): Boolean {
        val now = LocalDateTime.now()
        return connectCodeExpirationTime > now
    }
}
