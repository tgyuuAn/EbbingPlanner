package com.tgyuu.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.navigation.SettingRoute
import com.tgyuu.setting.SettingRoute

fun NavGraphBuilder.settingNavigation() {
    composable<SettingRoute> {
        SettingRoute()
    }
}
