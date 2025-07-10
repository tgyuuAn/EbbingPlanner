package com.tgyuu.domain.model

enum class Theme(val lightBg: Int, val darkBg: Int) {
    NORMAL(0xFFFFFFFF.toInt(), 0xFF262729.toInt()),
    FOREST(0xFFE8F5E9.toInt(), 0xFF1B5E20.toInt()),
    SUNSET(0xFFFFF3E0.toInt(), 0xFF4E342E.toInt()),
    MARINE(0xFFEAF3F8.toInt(), 0xFF263444.toInt()),
    LILAC(0xFFF2F0FF.toInt(), 0xFF1F1D2E.toInt());

    companion object {
        fun create(value: String): Theme = entries.firstOrNull {
            it.name.equals(value, ignoreCase = true)
        } ?: NORMAL
    }
}
