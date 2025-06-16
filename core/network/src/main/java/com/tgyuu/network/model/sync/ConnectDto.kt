package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class ConnectDto(
    val uuid: String,
    val connectCode: String,
    val connectCodeExpirationTime: Date,
) {
    fun toDomain() = ConnectInfo(
        uuid = uuid,
        connectCode = connectCode,
        connectCodeExpirationTime = connectCodeExpirationTime.toLocalDateTime(),
    )
}
