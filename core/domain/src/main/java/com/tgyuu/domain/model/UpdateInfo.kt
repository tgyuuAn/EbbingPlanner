package com.tgyuu.domain.model

data class UpdateInfo(
    val minVersion: String,
    val noticeMsgs: Map<String, String> = emptyMap(),
)
