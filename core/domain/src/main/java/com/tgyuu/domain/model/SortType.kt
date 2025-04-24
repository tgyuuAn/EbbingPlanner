package com.tgyuu.domain.model

enum class SortType(val displayName: String) {
    CREATED("생성순"),
    NAME("이름순"),
    PRIORITY("우선순");

    companion object {
        fun create(value: String): SortType = SortType.entries.find { it.name == value } ?: CREATED
    }
}
