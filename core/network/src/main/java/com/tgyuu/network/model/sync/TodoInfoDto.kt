package com.tgyuu.network.model.sync

import com.tgyuu.domain.model.sync.TodoInfoForSync
import com.tgyuu.network.toDate
import com.tgyuu.network.toLocalDate
import com.tgyuu.network.toLocalDateTime
import java.util.Date

data class TodoInfoDto(
    val id: Int,
    val title: String,
    val tagId: Int,
    val createdAt: Date,
    val updatedAt: Date,
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
