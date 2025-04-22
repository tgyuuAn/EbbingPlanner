package com.tgyuu.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object HomeBaseRoute : Route

sealed class HomeGraph : Route {
    @Serializable
    data object HomeRoute : HomeGraph()

    @Serializable
    data object AddTodoRoute : HomeGraph()
}

@Serializable
data object DashboardRoute : Route

@Serializable
data object SettingRoute : Route
