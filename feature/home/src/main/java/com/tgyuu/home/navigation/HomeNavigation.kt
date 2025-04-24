package com.tgyuu.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.home.graph.addag.AddTagRoute
import com.tgyuu.home.graph.addtodo.AddTodoRoute
import com.tgyuu.home.graph.edittodo.EditTodoRoute
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.HomeGraph.AddTagRoute
import com.tgyuu.navigation.HomeGraph.AddTodoRoute
import com.tgyuu.navigation.HomeGraph.HomeRoute
import java.time.LocalDate

fun NavGraphBuilder.homeGraph() {
    navigation<HomeBaseRoute>(startDestination = HomeGraph.HomeRoute()) {
        composable<HomeRoute> { backStackEntry ->
            val workedDate = backStackEntry.toRoute<HomeRoute>()
                .workedDate?.toLocalDateOrThrow() ?: LocalDate.now()

            com.tgyuu.home.graph.main.HomeRoute(workedDate = workedDate)
        }

        composable<AddTodoRoute> {
            AddTodoRoute()
        }

        composable<HomeGraph.EditTodoRoute> {
            EditTodoRoute()
        }

        composable<AddTagRoute> {
            AddTagRoute()
        }
    }
}
