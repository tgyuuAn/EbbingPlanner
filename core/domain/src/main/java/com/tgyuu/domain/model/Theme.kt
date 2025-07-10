package com.tgyuu.domain.model

enum class Theme {
    NORMAL,
    DARK,
    FOREST,
    FOREST_DARK,
    SUNSET,
    SUNSET_DARK,
    PASTEL,
    PASTEL_DARK;

    companion object {
        fun create(value: String): Theme = Theme.entries
            .firstOrNull { it.name == value } ?: NORMAL
    }
}
