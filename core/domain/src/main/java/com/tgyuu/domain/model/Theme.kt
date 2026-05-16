package com.tgyuu.domain.model

enum class Theme {
    NORMAL,
    FOREST,
    SUNSET,
    MARINE,
    LILAC;

    companion object {
        fun create(value: String): Theme = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: NORMAL
    }
}
