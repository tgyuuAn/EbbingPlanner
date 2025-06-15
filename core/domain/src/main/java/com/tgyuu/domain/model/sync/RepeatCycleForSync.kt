package com.tgyuu.domain.model.sync

import java.time.LocalDateTime

data class RepeatCycleForSync(
    val id: Int,
    val intervals: List<Int>,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
)
