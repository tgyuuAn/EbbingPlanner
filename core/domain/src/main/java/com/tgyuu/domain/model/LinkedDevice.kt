package com.tgyuu.domain.model

import java.time.LocalDate

data class LinkedDevice(
    val id: Int,
    val uuid: String,
    val lastUsedAt: LocalDate,
)
