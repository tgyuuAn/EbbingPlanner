package com.tgyuu.network.model.sync

import com.tgyuu.common.now
import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import java.util.Date

data class ConnectDto(
    val uuid: String = "",
    val connectCode: String = "",
    val connectCodeExpirationTime: Date = LocalDateTime.now().toDate(),
) {
    fun toDomain() = ConnectInfo(
        uuid = uuid,
        connectCode = connectCode,
        connectCodeExpirationTime = connectCodeExpirationTime.toLocalDateTime(),
    )
}
