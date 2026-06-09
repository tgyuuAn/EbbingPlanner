package com.tgyuu.network.model.sync

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime

@Serializable
data class SyncInfoDto(
    val uuid: String = "",
    @SerialName("connected_uuid") val connectedUuid: String? = null,
    @SerialName("last_updated_at") val lastUpdatedAt: String? = null,
) {
    fun toDomain(): ZonedDateTime? = lastUpdatedAt?.let { value ->
        runCatching {
            ZonedDateTime.parse(value).withZoneSameInstant(ZoneId.systemDefault())
        }.getOrNull()
    }
}
