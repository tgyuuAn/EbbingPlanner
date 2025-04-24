package com.tgyuu.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.puzzle.setting.graph.webview.WebViewRoute
import com.tgyuu.navigation.SettingBaseRoute
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.SettingGraph.SettingRoute
import com.tgyuu.setting.graph.main.SettingRoute

fun NavGraphBuilder.settingGraph() {
    navigation<SettingBaseRoute>(startDestination = SettingRoute) {
        composable<SettingRoute> {
            SettingRoute()
        }

        composable<SettingGraph.WebViewRoute> { backStackEntry ->
            val webView = backStackEntry.toRoute<SettingGraph.WebViewRoute>()
            WebViewRoute(
                title = webView.title,
                url = webView.url,
            )
        }
    }
}
