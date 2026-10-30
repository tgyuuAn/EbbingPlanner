package com.tgyuu.shared.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_nav_home
import ebbingplanner.shared.generated.resources.ic_nav_schedule
import ebbingplanner.shared.generated.resources.ic_nav_setting
import ebbingplanner.shared.generated.resources.nav_home
import ebbingplanner.shared.generated.resources.nav_schedule
import ebbingplanner.shared.generated.resources.nav_setting
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal enum class TopLevelTab(
    val tabletIconRes: DrawableResource,
    val labelRes: StringResource,
) {
    HOME(Res.drawable.ic_nav_home, Res.string.nav_home),
    SCHEDULE(Res.drawable.ic_nav_schedule, Res.string.nav_schedule),
    SETTING(Res.drawable.ic_nav_setting, Res.string.nav_setting),
}

/** 태블릿 가로 모드: 화면 왼쪽에 고정되는 내비게이션 (Android AppNavigationRail과 동일 수치) */
@Composable
internal fun TabletNavigationRail(
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = EbbingTheme.colors.light2

    Column(
        modifier = modifier
            .width(100.dp)
            .fillMaxHeight()
            .drawBehind {
                val x = size.width - 0.5.dp.toPx()
                drawLine(strokeColor, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
            }
            .padding(vertical = 18.dp),
    ) {
        TopLevelTab.entries.forEach { tab ->
            TabletNavigationItem(
                tab = tab,
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** 태블릿 세로 모드: 화면 하단에 고정되는 내비게이션 */
@Composable
internal fun TabletBottomBar(
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = EbbingTheme.colors.light2

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val y = 0.5.dp.toPx()
                drawLine(strokeColor, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            }
            .navigationBarsPadding()
            .padding(horizontal = 120.dp),
    ) {
        TopLevelTab.entries.forEach { tab ->
            TabletNavigationItem(
                tab = tab,
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 300.dp),
            )
        }
    }
}

@Composable
private fun TabletNavigationItem(
    tab: TopLevelTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor = if (selected) EbbingTheme.colors.black else EbbingTheme.colors.dark3
    val labelColor = if (selected) EbbingTheme.colors.black else EbbingTheme.colors.dark2

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(min = 100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(top = 14.dp, bottom = 18.dp),
    ) {
        Icon(
            painter = painterResource(tab.tabletIconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )

        Text(
            text = stringResource(tab.labelRes),
            style = EbbingTheme.typography.headingSB,
            color = labelColor,
            maxLines = 1,
        )
    }
}
