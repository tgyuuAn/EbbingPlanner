package com.tgyuu.shared.domain.model

enum class SortType {
    CREATED,
    NAME,
    PRIORITY;

    companion object {
        fun create(value: String): SortType = SortType.entries.find { it.name == value } ?: CREATED
    }
}
