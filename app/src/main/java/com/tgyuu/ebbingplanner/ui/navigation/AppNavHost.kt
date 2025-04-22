package com.tgyuu.ebbingplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.tgyuu.dashboard.navigation.dashboardNavigation
import com.tgyuu.home.navigation.homeGraph
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.setting.navigation.settingNavigation

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier,
    ) {
        homeGraph()
        dashboardNavigation()
        settingNavigation()
    }
}
