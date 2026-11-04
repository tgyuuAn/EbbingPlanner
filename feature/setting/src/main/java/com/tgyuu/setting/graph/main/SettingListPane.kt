package com.tgyuu.setting.graph.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

/** 열려 있는 메뉴 행을 강조해요. 목록의 좌우 여백까지 덮어 패널 폭을 꽉 채워요 */
internal fun Modifier.settingMenuHighlight(menu: SettingMenu): Modifier = composed {
    if (LocalSelectedSettingMenu.current == menu) {
        val color = EbbingTheme.colors.fillSelected
        val horizontalPadding = LocalSettingHorizontalPadding.current

        drawBehind {
            val extend = horizontalPadding.toPx()
            drawRect(
                color = color,
                topLeft = Offset(-extend, 0f),
                size = Size(size.width + extend * 2, size.height),
            )
        }
    } else {
        this
    }
}

internal val SettingTabletListPanePadding: Dp = 32.dp
internal val SettingTabletPortraitPadding: Dp = 60.dp
