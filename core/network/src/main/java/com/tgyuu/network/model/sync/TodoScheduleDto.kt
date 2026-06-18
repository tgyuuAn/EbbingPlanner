package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoScheduleForSync
import com.tgyuu.network.util.toLocalDateTimeFromUtc
import com.tgyuu.network.util.toUtcIsoString
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

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
    fun toDomain(): TodoScheduleForSync = TodoScheduleForSync(
        id = id,
        infoId = infoId,
        date = LocalDate.parse(date),
        memo = memo,
        priority = priority,
        isDone = isDone,
        createdAt = LocalDate.parse(createdAt),
        isDeleted = isDeleted,
        updatedAt = updatedAt.toLocalDateTimeFromUtc(),
    )
}

fun TodoScheduleForSync.toDto(uuid: String): TodoScheduleDto = TodoScheduleDto(
    id = id,
    uuid = uuid,
    infoId = infoId,
    date = date.toString(),
    memo = memo,
    priority = priority,
    isDone = isDone,
    isDeleted = isDeleted,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toUtcIsoString(),
)
