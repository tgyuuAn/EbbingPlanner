package com.tgyuu.sync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.navigation.SyncRoute
import com.tgyuu.sync.SyncRoute

fun NavGraphBuilder.syncNavigation() {
    composable<SyncRoute> {
        SyncRoute()
    }
}
