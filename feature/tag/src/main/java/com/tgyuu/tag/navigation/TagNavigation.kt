package com.tgyuu.tag.navigation

import androidx.navigation.NavGraphBuilder
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
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

        dialog<TagGraph.AddTagPopupRoute>(
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            AddTagRoute(isPopup = true)
        }

        composable<TagGraph.EditTagRoute> {
            EditTagRoute()
        }
    }
}
