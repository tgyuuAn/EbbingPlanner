package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectDto(
    val uuid: String = "",
    @SerialName("connect_code") val connectCode: String = "",
    @SerialName("expiration_time") val expirationTime: String = "",
) {
    fun toDomain() = ConnectInfo(
        uuid = uuid,
        connectCode = connectCode,
        connectCodeExpirationTime = expirationTime.toLocalDateTimeFromUtc(),
    )
}
