package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectDto(
    val uuid: String = "",
    @SerialName("connect_code") val connectCode: String = "",
    @SerialName("expiration_time") val expirationTime: String = "",
    @SerialName("device_name") val deviceName: String = "",
    @SerialName("connected_uuid") val connectedUuid: String? = null,
    @SerialName("connected_device_name") val connectedDeviceName: String? = null,
) {
    fun toDomain() = ConnectInfo(
        uuid = uuid,
        connectCode = connectCode,
        connectCodeExpirationTime = expirationTime.toLocalDateTimeFromUtc().toKotlinLocalDateTime(),
        deviceName = deviceName,
        connectedUuid = connectedUuid,
    )
}
