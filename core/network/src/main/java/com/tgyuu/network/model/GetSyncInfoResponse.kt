package com.tgyuu.network.model

import com.google.firebase.Timestamp
import com.tgyuu.network.toZonedDateTimeOrNull
import java.time.ZonedDateTime

data class GetSyncInfoResponse(
    val lastUpdatedAt: Timestamp? = null
) {
    fun toDomain(): ZonedDateTime? = lastUpdatedAt?.toZonedDateTimeOrNull()
}
