package com.tgyuu.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.dashboard.DashboardRoute
import com.tgyuu.navigation.DashboardRoute

fun NavGraphBuilder.dashboardNavigation() {
    composable<DashboardRoute> {
        DashboardRoute()
    }
}
