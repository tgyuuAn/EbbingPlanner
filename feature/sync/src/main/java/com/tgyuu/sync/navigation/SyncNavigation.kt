package com.tgyuu.sync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tgyuu.navigation.SyncBaseRoute
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.sync.graph.download.DownloadRoute
import com.tgyuu.sync.graph.link.LinkRoute
import com.tgyuu.sync.graph.main.SyncMainRoute
import com.tgyuu.sync.graph.upload.UploadRoute

fun NavGraphBuilder.syncNavigation() {
    navigation<SyncBaseRoute>(startDestination = SyncGraph.SyncMainRoute) {
        composable<SyncGraph.SyncMainRoute> {
            SyncMainRoute()
        }

        composable<SyncGraph.UploadRoute> {
            UploadRoute()
        }

        composable<SyncGraph.DownloadRoute> {
            DownloadRoute()
        }

        composable<SyncGraph.LinkRoute> {
            LinkRoute()
        }
    }
}
