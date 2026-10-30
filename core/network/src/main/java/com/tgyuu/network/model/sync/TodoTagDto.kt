package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

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
        updatedAt = updatedAt.toLocalDateTimeFromUtc().toKotlinLocalDateTime(),
    )
}

fun TodoTagForSync.toDto(uuid: String): TodoTagDto = TodoTagDto(
    id = id,
    uuid = uuid,
    name = name,
    color = color,
    isDeleted = isDeleted,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toJavaLocalDateTime().toUtcIsoString(),
)
