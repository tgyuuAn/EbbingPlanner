package com.tgyuu.ebbingplanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
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

    val requireNetworkConnection: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.networkRequiredRoutes.any {
                d.route?.startsWith(
                    it.qualifiedName ?: ""
                ) == true
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
}
