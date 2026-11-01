package com.tgyuu.ebbingplanner.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.HOME
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.SCHEDULE
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.SETTING
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.Route
import com.tgyuu.navigation.ScheduleRoute
import com.tgyuu.navigation.SettingGraph.SettingRoute

/** 태블릿 가로 모드에서 화면 왼쪽에 고정되는 내비게이션 */
@Composable
internal fun AppNavigationRail(
    selectedDestination: TopLevelDestination,
    navigateToDestination: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = EbbingTheme.colors.strokeNormal

    Column(
        modifier = modifier
            .width(100.dp)
            .fillMaxHeight()
            .drawBehind {
                val x = size.width - 0.5.dp.toPx()
                drawLine(
                    color = strokeColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(vertical = 18.dp),
    ) {
        TopLevelDestination.topLevelDestinations.forEach { destination ->
            TabletNavigationItem(
                destination = destination,
                selected = destination == selectedDestination,
                onClick = { navigateToDestination(destination.toRoute()) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** 태블릿 세로 모드에서 화면 하단에 고정되는 내비게이션 */
@Composable
internal fun AppTabletBottomBar(
    selectedDestination: TopLevelDestination,
    navigateToDestination: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strokeColor = EbbingTheme.colors.strokeNormal

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val y = 0.5.dp.toPx()
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .padding(horizontal = 120.dp),
    ) {
        TopLevelDestination.topLevelDestinations.forEach { destination ->
            TabletNavigationItem(
                destination = destination,
                selected = destination == selectedDestination,
                onClick = { navigateToDestination(destination.toRoute()) },
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = 300.dp),
            )
        }
    }
}

@Composable
private fun TabletNavigationItem(
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconColor: Color
    val labelColor: Color
    if (selected) {
        iconColor = EbbingTheme.colors.fillFocused
        labelColor = EbbingTheme.colors.textOnBackground
    } else {
        iconColor = EbbingTheme.colors.fillDisabled
        labelColor = EbbingTheme.colors.textSub
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(min = 100.dp)
            .semantics {
                this.selected = selected
                role = Role.Tab
            }
            .clickable(onClick = onClick)
            .padding(top = 14.dp, bottom = 18.dp),
    ) {
        Icon(
            painter = painterResource(destination.tabletIconDrawableId),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )

        Text(
            text = stringResource(destination.titleRes),
            style = EbbingTheme.typography.heading14SB,
            color = labelColor,
            maxLines = 1,
        )
    }
}

private fun TopLevelDestination.toRoute(): Route = when (this) {
    HOME -> HomeRoute()
    SCHEDULE -> ScheduleRoute
    SETTING -> SettingRoute
}
