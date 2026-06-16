package com.tgyuu.ebbingplanner.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.tgyuu.common.util.NoRippleInteractionSource
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.SCHEDULE
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.HOME
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination.SETTING
import com.tgyuu.ebbingplanner.ui.isRouteInHierarchy
import com.tgyuu.navigation.ScheduleRoute
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.Route
import com.tgyuu.navigation.SettingGraph.SettingRoute

@Composable
internal fun AppBottomBar(
    currentDestination: NavDestination?,
    navigateToBottomBarDestination: (Route) -> Unit,
    modifier: Modifier = Modifier,
) {
    val black = EbbingTheme.colors.textOnBackground

    Box(
        modifier = modifier
            .height(71.dp)
            .drawBehind {
                val shadowHeight = 3.dp.toPx()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            black.copy(alpha = 0.1f),
                        ),
                        startY = 0f,
                        endY = shadowHeight
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = Size(size.width, shadowHeight)
                )
            }
    ) {
        NavigationBar(
            containerColor = EbbingTheme.colors.background,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(68.dp),
        ) {
            TopLevelDestination.topLevelDestinations.forEach { topLevelRoute ->
                NavigationBarItem(
                    icon = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 2.dp),
                        ) {
                            Icon(
                                painter = painterResource(topLevelRoute.iconDrawableId),
                                contentDescription = stringResource(topLevelRoute.contentDescriptionRes),
                                modifier = Modifier.size(32.dp),
                            )

                            Text(
                                text = stringResource(topLevelRoute.titleRes),
                                style = EbbingTheme.typography.caption12R,
                            )
                        }
                    },
                    alwaysShowLabel = false,
                    selected = currentDestination.isRouteInHierarchy(topLevelRoute.route),
                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                        selectedIconColor = EbbingTheme.colors.textOnBackground,
                        unselectedIconColor = EbbingTheme.colors.textDisabled,
                        selectedTextColor = EbbingTheme.colors.textOnBackground,
                        unselectedTextColor = EbbingTheme.colors.textDisabled,
                        indicatorColor = Color.Transparent,
                    ),
                    interactionSource = remember { NoRippleInteractionSource() },
                    onClick = {
                        when (topLevelRoute) {
                            HOME -> navigateToBottomBarDestination(HomeRoute())
                            SCHEDULE -> navigateToBottomBarDestination(ScheduleRoute)
                            SETTING -> navigateToBottomBarDestination(SettingRoute)
                        }
                    }
                )
            }
        }
    }
}
