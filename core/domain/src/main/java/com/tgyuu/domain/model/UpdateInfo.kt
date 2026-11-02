package com.tgyuu.domain.model

data class UpdateState(
    val soft: UpdateInfo? = null,
    val hard: UpdateInfo? = null,
)

data class UpdateInfo(
    val minVersion: String,
    val noticeMsgs: Map<String, String> = emptyMap(),
) {
    fun noticeMsg(language: String): String? = noticeMsgs[language]?.takeIf { it.isNotBlank() }
}
