package com.tgyuu.shared.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 앱 뼈대(RootContent)가 창 크기로 계산해 내려주는 레이아웃 정보.
 * - isTablet: 너비가 [LayoutConstants.TABLET_BREAKPOINT] 이상
 * - isLandscape: 너비가 높이보다 김 (태블릿 가로 = 레일, 세로 = 하단 탭바)
 */
@Immutable
data class WindowLayout(
    val isTablet: Boolean = false,
    val isLandscape: Boolean = false,
)

val LocalWindowLayout = staticCompositionLocalOf { WindowLayout() }
