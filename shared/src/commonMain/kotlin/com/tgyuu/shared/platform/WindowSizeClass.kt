package com.tgyuu.shared.platform

enum class WindowWidthSizeClass {
    Compact,
    Medium,
    Expanded,
}

enum class WindowHeightSizeClass {
    Compact,
    Medium,
    Expanded,
}

data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass,
) {
    companion object {
        fun calculate(widthDp: Float, heightDp: Float): WindowSizeClass {
            val widthSizeClass = when {
                widthDp < 600f -> WindowWidthSizeClass.Compact
                widthDp < 840f -> WindowWidthSizeClass.Medium
                else -> WindowWidthSizeClass.Expanded
            }

            val heightSizeClass = when {
                heightDp < 480f -> WindowHeightSizeClass.Compact
                heightDp < 900f -> WindowHeightSizeClass.Medium
                else -> WindowHeightSizeClass.Expanded
            }

            return WindowSizeClass(widthSizeClass, heightSizeClass)
        }
    }
}

val WindowSizeClass.isPhone: Boolean
    get() = widthSizeClass == WindowWidthSizeClass.Compact

val WindowSizeClass.isTablet: Boolean
    get() = widthSizeClass != WindowWidthSizeClass.Compact
