package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

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
        updatedAt = updatedAt.toLocalDateTimeFromUtc().toKotlinLocalDateTime(),
        restDays = restDays,
    )
}

fun TodoInfoForSync.toDto(uuid: String) = TodoInfoDto(
    id = id,
    uuid = uuid,
    title = title,
    tagId = tagId,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toJavaLocalDateTime().toUtcIsoString(),
    restDays = restDays,
)
