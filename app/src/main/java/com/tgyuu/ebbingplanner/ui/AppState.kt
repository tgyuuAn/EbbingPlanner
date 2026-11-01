package com.tgyuu.ebbingplanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tgyuu.analytics.TrackNavigationDestination
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.navigation.Route
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.sync.network.NetworkMonitor

@Composable
fun rememberEbbingAppState(
    navController: NavHostController,
    networkMonitor: NetworkMonitor,
    bottomSheetState: EbbingBottomSheetState,
): EbbingAppState {
    TrackNavigationDestination(navController)

    return remember(
        navController,
        networkMonitor,
        bottomSheetState,
    ) {
        EbbingAppState(
            navController = navController,
            bottomSheetState = bottomSheetState,
            networkMonitor = networkMonitor,
        )
    }
}

@Stable
class EbbingAppState(
    val navController: NavHostController,
    val bottomSheetState: EbbingBottomSheetState,
    private val networkMonitor: NetworkMonitor,
) {
    val networkState @Composable get() = networkMonitor.networkState.collectAsStateWithLifecycle().value

    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val shouldHideBottomBar: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.bottomBarHiddenRoutes.any {
                d.route?.startsWith(it.qualifiedName ?: "") == true
            }
        } ?: false

    val shouldHideTabletNavigation: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.tabletNavigationHiddenRoutes.any {
                d.route?.startsWith(it.qualifiedName ?: "") == true
            }
        } ?: false

    val requireNetworkConnection: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.networkRequiredRoutes.any {
                d.route?.startsWith(it.qualifiedName ?: "") == true
            }
        } ?: false

    val isRootRoute: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.rootRoutes.any { d.route?.startsWith(it.qualifiedName ?: "") == true }
        } ?: false

    val isWebViewRoute: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            d.route?.startsWith(SettingGraph.WebViewRoute::class.qualifiedName ?: "") == true
        } ?: false

    fun navigate(route: Route) = navController.navigate(route)

    // 하위 화면에서 탭을 눌러도 백스택이 쌓이지 않도록 시작 화면까지 정리하고 이동해요
    fun navigateToTopLevelDestination(route: Route) = navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id)
        launchSingleTop = true
    }
}
