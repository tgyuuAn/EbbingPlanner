package com.tgyuu.tag.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.navigation.TagBaseRoute
import com.tgyuu.navigation.TagGraph
import com.tgyuu.tag.graph.addtag.AddTagRoute
import com.tgyuu.tag.graph.edittag.EditTagRoute
import com.tgyuu.tag.graph.main.TagRoute

fun NavGraphBuilder.tagGraph() {
    navigation<TagBaseRoute>(startDestination = TagGraph.TagRoute) {
        composable<TagGraph.TagRoute> {
            TagRoute()
        }

        composable<TagGraph.AddTagRoute> {
            AddTagRoute()
        }

        composable<TagGraph.EditTagRoute> {
            EditTagRoute()
        }
    }
}
