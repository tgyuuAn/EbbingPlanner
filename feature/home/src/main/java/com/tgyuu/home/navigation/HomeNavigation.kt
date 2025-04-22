package com.tgyuu.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.home.graph.add.AddTodoRoute
import com.tgyuu.home.graph.main.HomeRoute
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.HomeRoute

fun NavGraphBuilder.homeGraph() {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeRoute()
        }

        composable<AddTodoRoute> {
            AddTodoRoute()
        }
    }
}
