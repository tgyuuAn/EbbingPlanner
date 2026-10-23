@file:OptIn(ExperimentalSerializationApi::class)

package com.tgyuu.shared.data.source

import com.tgyuu.shared.domain.model.sync.ConnectInfo
import com.tgyuu.shared.domain.model.sync.RepeatCycleForSync
import com.tgyuu.shared.domain.model.sync.TodoInfoForSync
import com.tgyuu.shared.domain.model.sync.TodoScheduleForSync
import com.tgyuu.shared.domain.model.sync.TodoTagForSync
import kotlinx.datetime.LocalDate
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncInfoDto(
    val uuid: String = "",
    @SerialName("connected_uuid") val connectedUuid: String? = null,
    @SerialName("last_updated_at") val lastUpdatedAt: String? = null,
    @SerialName("device_name") val deviceName: String = "",
) {
    fun toLastUpdatedAt() = lastUpdatedAt?.let { runCatching { it.toLocalDateTimeFromUtc() }.getOrNull() }
}

@Serializable
data class TodoScheduleDto(
    @EncodeDefault val id: Int = -1,
    @EncodeDefault val uuid: String = "",
    @EncodeDefault @SerialName("info_id") val infoId: Int = -1,
    @EncodeDefault val date: String = "",
    @EncodeDefault val memo: String = "",
    @EncodeDefault val priority: Int = 0,
    @EncodeDefault @SerialName("is_done") val isDone: Boolean = false,
    @EncodeDefault @SerialName("is_deleted") val isDeleted: Boolean = false,
    @EncodeDefault @SerialName("created_at") val createdAt: String = "",
    @EncodeDefault @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("uploaded_at") val uploadedAt: String? = null,
) {
    fun toDomain() = TodoScheduleForSync(
        id = id,
        infoId = infoId,
        date = LocalDate.parse(date),
        memo = memo,
        isPinned = priority != 0,
        isDone = isDone,
        createdAt = LocalDate.parse(createdAt),
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
    )
}

fun TodoScheduleForSync.toDto(uuid: String) = TodoScheduleDto(
    id = id,
    uuid = uuid,
    infoId = infoId,
    date = date.toString(),
    memo = memo,
    priority = if (isPinned) 1 else 0,
    isDone = isDone,
    isDeleted = isDeleted,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toUtcIsoString(),
)

@Serializable
data class TodoInfoDto(
    @EncodeDefault val id: Int = -1,
    @EncodeDefault val uuid: String = "",
    @EncodeDefault val title: String = "",
    @EncodeDefault @SerialName("tag_id") val tagId: Int = -1,
    @EncodeDefault @SerialName("rest_days") val restDays: String = "",
    @EncodeDefault @SerialName("created_at") val createdAt: String = "",
    @EncodeDefault @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("uploaded_at") val uploadedAt: String? = null,
) {
    fun toDomain() = TodoInfoForSync(
        id = id,
        title = title,
        tagId = tagId,
        createdAt = LocalDate.parse(createdAt),
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
        restDays = restDays,
    )
}

fun TodoInfoForSync.toDto(uuid: String) = TodoInfoDto(
    id = id,
    uuid = uuid,
    title = title,
    tagId = tagId,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toUtcIsoString(),
    restDays = restDays,
)

@Serializable
data class TodoTagDto(
    @EncodeDefault val id: Int = -1,
    @EncodeDefault val uuid: String = "",
    @EncodeDefault val name: String = "",
    @EncodeDefault val color: Int = -1,
    @EncodeDefault @SerialName("is_deleted") val isDeleted: Boolean = false,
    @EncodeDefault @SerialName("created_at") val createdAt: String = "",
    @EncodeDefault @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("uploaded_at") val uploadedAt: String? = null,
) {
    fun toDomain() = TodoTagForSync(
        id = id,
        name = name,
        color = color,
        createdAt = LocalDate.parse(createdAt),
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
    )
}

fun TodoTagForSync.toDto(uuid: String) = TodoTagDto(
    id = id,
    uuid = uuid,
    name = name,
    color = color,
    isDeleted = isDeleted,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toUtcIsoString(),
)

@Serializable
data class RepeatCycleDto(
    @EncodeDefault val id: Int = -1,
    @EncodeDefault val uuid: String = "",
    @EncodeDefault val intervals: List<Int> = emptyList(),
    @EncodeDefault @SerialName("is_deleted") val isDeleted: Boolean = false,
    @EncodeDefault @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("uploaded_at") val uploadedAt: String? = null,
) {
    fun toDomain() = RepeatCycleForSync(
        id = id,
        intervals = intervals,
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
    )
}

fun RepeatCycleForSync.toDto(uuid: String) = RepeatCycleDto(
    id = id,
    uuid = uuid,
    intervals = intervals,
    isDeleted = isDeleted,
    updatedAt = updatedAt.toUtcIsoString(),
)

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
