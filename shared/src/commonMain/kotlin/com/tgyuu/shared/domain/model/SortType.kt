package com.tgyuu.shared.domain.model

enum class SortType {
    CREATED,
    BY_TAG;

    companion object {
        // 과거 저장값("NAME"/"PRIORITY")은 CREATED로 폴백
        fun create(value: String): SortType = SortType.entries.find { it.name == value } ?: CREATED
    }
}
