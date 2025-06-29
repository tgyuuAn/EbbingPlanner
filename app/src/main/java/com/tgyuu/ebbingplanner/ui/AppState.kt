package com.tgyuu.ebbingplanner.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.navigation.Route
import com.tgyuu.sync.network.NetworkMonitor

@Composable
fun rememberEbbingAppState(
    navController: NavHostController,
    networkMonitor: NetworkMonitor,
    configRepository: ConfigRepository,
): EbbingAppState {
    val context = LocalContext.current
    var shouldUpdate by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }

    LaunchedEffect(Unit) {
        updateInfo = configRepository.getUpdateInfo().getOrNull()
        shouldUpdate = shouldShowUpdateDialog(context, updateInfo)
    }

    return EbbingAppState(
        shouldUpdate = shouldUpdate,
        updateInfo = updateInfo,
        navController = navController,
        networkMonitor = networkMonitor,
    )
}

private fun shouldShowUpdateDialog(context: Context, info: UpdateInfo?): Boolean {
    if (info == null) return false

    val currentVersion =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    return checkShouldUpdate(currentVersion!!, info.minVersion)
}

private fun checkShouldUpdate(currentVersion: String, minVersion: String): Boolean {
    val current = normalizeVersion(currentVersion)
    val min = normalizeVersion(minVersion)
    return current.zip(min).any { (cur, min) -> cur < min }
}

private fun normalizeVersion(version: String): List<Int> = version.split('.')
    .map { it.toIntOrNull() ?: 0 }
    .let { if (it.size == 2) it + 0 else it }

@Stable
class EbbingAppState(
    val shouldUpdate: Boolean,
    val updateInfo: UpdateInfo?,
    val navController: NavHostController,
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
                d.route?.startsWith(it.qualifiedName ?: "") == true
            }
        } ?: false

    val isRootRoute: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { d ->
            AppUiPolicy.rootRoutes.any {
                d.route?.startsWith(it.qualifiedName ?: "") == true
            }
        } ?: false

    fun navigate(route: Route) = navController.navigate(route)
}
