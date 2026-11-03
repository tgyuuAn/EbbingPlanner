package com.tgyuu.repeatcycle.navigation

import androidx.navigation.NavGraphBuilder
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
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

        dialog<RepeatCycleGraph.AddRepeatCyclePopupRoute>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            AddRepeatCycleRoute(isPopup = true)
        }

        composable<RepeatCycleGraph.EditRepeatCycleRoute> {
            EditRepeatCycleRoute()
        }
    }
}
