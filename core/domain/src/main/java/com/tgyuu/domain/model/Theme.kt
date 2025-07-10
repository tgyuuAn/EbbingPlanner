package com.tgyuu.domain.model

enum class Theme(val representativeColor: Int) {
    NORMAL(0xFF0F4C75.toInt()),
    DARK(0xFFBBE1FA.toInt()),
    FOREST(0xFF2E7D32.toInt()),
    FOREST_DARK(0xFF81C784.toInt()),
    SUNSET(0xFFF4511E.toInt()),
    SUNSET_DARK(0xFFFFAB91.toInt()),
    PASTEL(0xFF80DEEA.toInt()),
    PASTEL_DARK(0xFFB2EBF2.toInt());

    companion object {
        fun create(value: String): Theme = entries.firstOrNull { it.name == value } ?: NORMAL
    }
}
