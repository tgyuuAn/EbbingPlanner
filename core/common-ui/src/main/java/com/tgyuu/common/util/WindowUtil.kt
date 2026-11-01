package com.tgyuu.common.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** 현재 창이 가로로 더 긴지 여부. 멀티 윈도우에서도 기기 방향이 아닌 창 크기로 판단해요 */
@Composable
fun isLandscapeWindow(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}
