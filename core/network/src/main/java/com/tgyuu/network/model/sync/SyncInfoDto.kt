package com.tgyuu.network.model.sync

import com.google.firebase.Timestamp
import com.tgyuu.network.toLocalDateTimeOrNull
import kotlinx.datetime.LocalDateTime

data class SyncInfoDto(val lastUpdatedAt: Timestamp? = null) {
    fun toDomain(): LocalDateTime? = lastUpdatedAt?.toLocalDateTimeOrNull()
}
