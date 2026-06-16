package com.tgyuu.domain.model

enum class CalendarDefaultView {
    MONTHLY,
    WEEKLY,
    DAILY;

    companion object {
        fun create(value: String): CalendarDefaultView =
            entries.find { it.name == value } ?: MONTHLY
    }
}
