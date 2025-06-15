package com.tgyuu.network.model.sync

import com.google.firebase.firestore.ServerTimestamp
import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.network.defaultDate
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoInfoDto(
    val id: Int = -1,
    val title: String = "",
    val tagId: Int = -1,
    val createdAt: Date = defaultDate,
    val updatedAt: Date = defaultDate,
    @ServerTimestamp var uploadedAt: Date? = null,
) {
    fun toDomain() = TodoInfoForSync(
        id = id,
        title = title,
        tagId = tagId,
        createdAt = createdAt.toLocalDate(),
        updatedAt = updatedAt.toLocalDateTime(),
    )
}

fun TodoInfoForSync.toDto() = TodoInfoDto(
    id = id,
    title = title,
    tagId = tagId,
    createdAt = createdAt.toDate(),
    updatedAt = updatedAt.toDate(),
)
