package com.tgyuu.memo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.memo.graph.addmemo.AddMemoRoute
import com.tgyuu.navigation.MemoBaseRoute
import com.tgyuu.navigation.MemoGraph

fun NavGraphBuilder.memoGraph() {
    navigation<MemoBaseRoute>(startDestination = MemoGraph.AddMemoRoute()) {
        composable<MemoGraph.AddMemoRoute> {
            AddMemoRoute()
        }

        composable<MemoGraph.EditMemoRoute> {
            EditMemoRoute()
        }
    }
}
