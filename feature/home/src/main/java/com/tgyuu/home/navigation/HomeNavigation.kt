package com.tgyuu.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.home.HomeRoute
import com.tgyuu.navigation.HomeRoute

fun NavGraphBuilder.homeNavigation() {
    composable<HomeRoute> {
        HomeRoute()
    }
}
