package com.tgyuu.domain.model

enum class SortType {
    CREATED,
    NAME;

    companion object {
        fun create(value: String): SortType = SortType.entries.find { it.name == value } ?: CREATED
    }
}
