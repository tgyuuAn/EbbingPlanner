package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoTagForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TodoTagDto(
    val id: Int = -1,
    val uuid: String = "",
    val name: String = "",
    val color: Int = -1,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
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

fun TodoTagForSync.toDto(uuid: String): TodoTagDto = TodoTagDto(
    id = id,
    uuid = uuid,
    name = name,
    color = color,
    isDeleted = isDeleted,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toUtcIsoString(),
)
