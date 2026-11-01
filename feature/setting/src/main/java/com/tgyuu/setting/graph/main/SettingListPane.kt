package com.tgyuu.setting.graph.main

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme

/** 태블릿 가로 2패널에서 오른쪽 상세 패널에 열려 있는 설정 메뉴 */
enum class SettingMenu { TAG, REPEAT_CYCLE, SYNC, RESTORE, THEME, WIDGET }

internal val LocalSelectedSettingMenu = compositionLocalOf<SettingMenu?> { null }
internal val LocalSettingHorizontalPadding = staticCompositionLocalOf { 20.dp }

/**
 * 태블릿 가로 모드에서 앱 뼈대가 왼쪽에 고정으로 띄우는 설정 목록.
 * 하위 화면은 오른쪽(NavHost)에 열리고, 열려 있는 메뉴는 [selectedMenu]로 강조해요.
 */
@Composable
fun SettingListPane(
    selectedMenu: SettingMenu?,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalSelectedSettingMenu provides selectedMenu) {
        SettingRoute(isListPane = true, modifier = modifier)
    }
}

internal fun Modifier.settingMenuHighlight(menu: SettingMenu): Modifier = composed {
    if (LocalSelectedSettingMenu.current == menu) {
        background(EbbingTheme.colors.fillSelected)
    } else {
        this
    }
}

internal val SettingTabletListPanePadding: Dp = 48.dp
internal val SettingTabletPortraitPadding: Dp = 60.dp
