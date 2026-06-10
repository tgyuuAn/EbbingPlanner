package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.RepeatCycleForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepeatCycleDto(
    val id: Int = -1,
    val uuid: String = "",
    val intervals: List<Int> = emptyList(),
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("updated_at") val updatedAt: String = "",
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
