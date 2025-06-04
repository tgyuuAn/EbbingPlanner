package com.tgyuu.repeatcycle.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.repeatcycle.graph.addrepeatcycle.AddRepeatCycleRoute
import com.tgyuu.repeatcycle.graph.editrepeatcycle.EditRepeatCycleRoute
import com.tgyuu.navigation.RepeatCycleBaseRoute
import com.tgyuu.navigation.RepeatCycleGraph

fun NavGraphBuilder.repeatCycleGraph() {
    navigation<RepeatCycleBaseRoute>(startDestination = RepeatCycleGraph.AddRepeatCycleRoute) {
        composable<RepeatCycleGraph.AddRepeatCycleRoute> {
            AddRepeatCycleRoute()
        }

        composable<RepeatCycleGraph.EditRepeatCycleRoute> {
            EditRepeatCycleRoute()
        }
    }
}
