package com.tgyuu.shared.domain.model

enum class CalendarDefaultView(val displayName: String) {
    MONTHLY("달 단위"),
    WEEKLY("주 단위"),
    DAILY("일 단위");

    companion object {
        fun create(value: String): CalendarDefaultView =
            entries.find { it.name == value } ?: MONTHLY
    }
}
