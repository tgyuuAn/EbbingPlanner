package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TodoInfoDto(
    val id: Int = -1,
    val uuid: String = "",
    val title: String = "",
    @SerialName("tag_id") val tagId: Int = -1,
    @SerialName("rest_days") val restDays: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
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
