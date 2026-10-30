package com.tgyuu.shared.ui.feature.setting

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

/** 태블릿 가로 2패널에서 오른쪽 상세 패널에 열려 있는 설정 메뉴 (Android SettingMenu와 동일) */
enum class SettingMenu { TAG, REPEAT_CYCLE, SYNC, RESTORE, THEME, WIDGET }

internal val LocalSelectedSettingMenu = compositionLocalOf<SettingMenu?> { null }
internal val LocalSettingHorizontalPadding = staticCompositionLocalOf { 20.dp }

internal val SettingTabletListPanePadding = 48.dp
internal val SettingTabletPortraitPadding = 60.dp
