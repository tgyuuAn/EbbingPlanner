package com.tgyuu.network.model

import com.tgyuu.domain.model.UpdateInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetUpdateInfoResponse(
    val minVersion: String = "1.0.0",
    // 언어 코드(ko, en, ja)별 안내 문구. 없는 언어는 앱의 기본 문구로 보여줘요.
    val noticeMsgs: Map<String, String> = emptyMap(),
    // 다국어 지원 이전 버전 호환용 한국어 문구
    val noticeMsg: String? = null,
) {
    fun toDomain() = UpdateInfo(
        minVersion = minVersion,
        noticeMsgs = buildMap {
            noticeMsg?.let { put(KOREAN, it) }
            putAll(noticeMsgs)
        },
    )

    private companion object {
        const val KOREAN = "ko"
    }
}
