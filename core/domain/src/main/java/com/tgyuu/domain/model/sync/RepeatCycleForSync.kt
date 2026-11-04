package com.tgyuu.domain.model.sync

import kotlinx.datetime.LocalDateTime

data class RepeatCycleForSync(
    val id: Int,
    val intervals: List<Int>,
    val isDeleted: Boolean,
    val updatedAt: LocalDateTime,
)
