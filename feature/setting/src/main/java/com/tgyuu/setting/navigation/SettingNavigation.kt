package com.tgyuu.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.puzzle.setting.graph.webview.WebViewRoute
import com.tgyuu.analytics.TrackScreenViewEvent
import com.tgyuu.navigation.SettingBaseRoute
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.SettingGraph.SettingRoute
import com.tgyuu.setting.graph.main.SettingRoute
import com.tgyuu.setting.graph.theme.ThemeRoute
import com.tgyuu.setting.graph.widget.WidgetRoute

fun NavGraphBuilder.settingGraph() {
    navigation<SettingBaseRoute>(startDestination = SettingRoute) {
        composable<SettingRoute> {
            TrackScreenViewEvent(key = Unit, screenName = "Setting")
            SettingRoute()
        }

        composable<SettingGraph.ThemeRoute> {
            ThemeRoute()
        }

        composable<SettingGraph.WidgetRoute> {
            WidgetRoute()
        }

        composable<SettingGraph.WebViewRoute> { backStackEntry ->
            TrackScreenViewEvent(key = Unit, screenName = "WebView")
            val webView = backStackEntry.toRoute<SettingGraph.WebViewRoute>()
            WebViewRoute(
                title = webView.title,
                url = webView.url,
            )
        }
    }
}
