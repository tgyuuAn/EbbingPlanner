package com.tgyuu.domain.model

data class UpdateState(
    val soft: UpdateInfo? = null,
    val hard: UpdateInfo? = null,
)

data class UpdateInfo(
    val minVersion: String,
    val noticeMsg: String,
)
