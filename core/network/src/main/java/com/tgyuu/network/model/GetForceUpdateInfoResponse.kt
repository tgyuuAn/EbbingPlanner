package com.tgyuu.network.model

import com.tgyuu.domain.model.UpdateInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetUpdateInfoResponse(
    val minVersion: String = "1.0.0",
    val noticeMsg: String = "여러분의 의견을 반영하여 사용성을 개선했습니다.\n지금 바로 업데이트해 보세요!",
) {
    fun toDomain() = UpdateInfo(
        minVersion = minVersion,
        noticeMsg = noticeMsg,
    )
}
