package com.tgyuu.domain.model

enum class ThemeColor {
    NORMAL;

    companion object {
        fun create(value: String): ThemeColor = ThemeColor.entries
            .firstOrNull { it.name == value } ?: NORMAL
    }
}
