package com.tgyuu.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.dashboard.DashBoardRoute
import com.tgyuu.navigation.DashBoardRoute

fun NavGraphBuilder.dashBoardNavigation() {
    composable<DashBoardRoute> {
        DashBoardRoute()
    }
}
