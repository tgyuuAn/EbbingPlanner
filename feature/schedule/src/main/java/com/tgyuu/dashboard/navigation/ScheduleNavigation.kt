package com.tgyuu.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.dashboard.ScheduleRoute
import com.tgyuu.navigation.ScheduleRoute

fun NavGraphBuilder.scheduleNavigation() {
    composable<ScheduleRoute> {
        ScheduleRoute()
    }
}
