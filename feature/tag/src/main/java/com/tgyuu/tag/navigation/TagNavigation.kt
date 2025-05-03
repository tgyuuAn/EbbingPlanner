package com.tgyuu.tag.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.navigation.TagBaseRoute
import com.tgyuu.navigation.TagGraph
import com.tgyuu.tag.graph.addtag.AddTagRoute

fun NavGraphBuilder.tagGraph() {
    navigation<TagBaseRoute>(startDestination = TagGraph.AddTagRoute) {
        composable<TagGraph.AddTagRoute> {
            AddTagRoute()
        }
    }
}
