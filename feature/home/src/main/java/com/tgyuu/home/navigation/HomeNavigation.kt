package com.tgyuu.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.home.graph.addag.AddTagRoute
import com.tgyuu.home.graph.addtodo.AddTodoRoute
import com.tgyuu.home.graph.main.HomeRoute
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.HomeGraph.AddTagRoute
import com.tgyuu.navigation.HomeGraph.AddTodoRoute

fun NavGraphBuilder.homeGraph() {
    navigation<HomeBaseRoute>(startDestination = HomeGraph.HomeRoute()) {
        composable<HomeGraph.HomeRoute> {
            HomeRoute()
        }

        composable<AddTodoRoute> {
            AddTodoRoute()
        }

        composable<AddTagRoute> {
            AddTagRoute()
        }
    }
}
