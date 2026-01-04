package com.tgyuu.network.model.sync

import com.tgyuu.common.now
import com.tgyuu.domain.model.sync.ConnectInfo
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import java.util.Date

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
        connectCodeExpirationTime = expirationTime.toLocalDateTimeFromUtc(),
        deviceName = deviceName,
        connectedUuid = connectedUuid,
    )
}
