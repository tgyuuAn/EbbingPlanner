package com.tgyuu.repeatcycle.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.navigation.RepeatCycleBaseRoute
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.repeatcycle.graph.addrepeatcycle.AddRepeatCycleRoute
import com.tgyuu.repeatcycle.graph.editrepeatcycle.EditRepeatCycleRoute
import com.tgyuu.repeatcycle.graph.main.RepeatCycleRoute

fun NavGraphBuilder.repeatCycleGraph() {
    navigation<RepeatCycleBaseRoute>(startDestination = RepeatCycleGraph.AddRepeatCycleRoute) {
        composable<RepeatCycleGraph.RepeatCycleRoute> {
            RepeatCycleRoute()
        }

        composable<RepeatCycleGraph.AddRepeatCycleRoute> {
            AddRepeatCycleRoute()
        }

        composable<RepeatCycleGraph.EditRepeatCycleRoute> {
            EditRepeatCycleRoute()
        }
    }
}
