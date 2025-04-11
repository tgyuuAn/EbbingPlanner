//package com.tgyuu.ebbingplanner.navigation
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.drawBehind
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavDestination
//import com.puzzle.common.ui.NoRippleInteractionSource
//import com.puzzle.designsystem.foundation.PieceTheme
//import com.puzzle.navigation.MatchingGraphBaseRoute
//import com.puzzle.navigation.ProfileGraph.MainProfileRoute
//import com.puzzle.navigation.SettingGraphBaseRoute
//import com.puzzle.navigation.isRouteInHierarchy
//import com.tgyuu.navigation.Route
//import com.tgyuu.navigation.isRouteInHierarchy
//
//@Composable
//internal fun AppBottomBar(
//    currentDestination: NavDestination?,
//    navigateToBottomNaviDestination: (Route) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    Box(
//        modifier = Modifier
//            .height(71.dp)
//            .drawBehind {
//                val shadowHeight = 3.dp.toPx()
//
//                drawRect(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color.Transparent,
//                            Color.Black.copy(alpha = 0.1f),
//                        ),
//                        startY = 0f,
//                        endY = shadowHeight
//                    ),
//                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
//                    size = Size(size.width, shadowHeight)
//                )
//            }
//    ) {
//        NavigationBar(
////            containerColor = PieceTheme.colors.white,
//            modifier = modifier
//                .align(Alignment.BottomCenter)
//                .height(68.dp),
//        ) {
//            TopLevelDestination.topLevelDestinations.forEach { topLevelRoute ->
//                NavigationBarItem(
//                    icon = {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.padding(top = 2.dp),
//                        ) {
//                            Icon(
//                                painter = painterResource(topLevelRoute.iconDrawableId),
//                                contentDescription = topLevelRoute.contentDescription,
//                                modifier = Modifier.size(32.dp),
//                            )
//
//                            Text(
//                                text = topLevelRoute.title,
//                                style = PieceTheme.typography.captionM,
//                            )
//                        }
//                    },
//                    alwaysShowLabel = false,
//                    selected = currentDestination.isRouteInHierarchy(topLevelRoute.route),
//                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
//                        selectedIconColor = PieceTheme.colors.primaryDefault,
//                        unselectedIconColor = PieceTheme.colors.dark3,
//                        selectedTextColor = PieceTheme.colors.primaryDefault,
//                        unselectedTextColor = PieceTheme.colors.dark3,
//                        indicatorColor = Color.Transparent,
//                    ),
//                    interactionSource = remember { NoRippleInteractionSource() },
//                    onClick = {
//                        when (topLevelRoute) {
//                            TopLevelDestination.MATCHING -> navigateToBottomNaviDestination(
//                                MatchingGraphBaseRoute
//                            )
//
//                            TopLevelDestination.PROFILE -> navigateToBottomNaviDestination(
//                                MainProfileRoute
//                            )
//
//                            TopLevelDestination.SETTING -> navigateToBottomNaviDestination(
//                                SettingGraphBaseRoute
//                            )
//                        }
//                    },
//                )
//            }
//        }
//    }
//}
