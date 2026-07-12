package com.tgyuu.domain.model

enum class SortType {
    CREATED,
    BY_TAG;

    companion object {
        fun create(value: String): SortType = SortType.entries.find { it.name == value } ?: CREATED
    }
}
