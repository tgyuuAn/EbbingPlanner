package com.tgyuu.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object HomeBaseRoute : Route

sealed class HomeGraph : Route {
    @Serializable
    data class HomeRoute(val addTodoDate: String? = null) : HomeGraph()

    @Serializable
    data class AddTodoRoute(val selectedDate: String) : HomeGraph()

    @Serializable
    data class EditTodoRoute(val scheduleId: Int) : HomeGraph()

    @Serializable
    data object AddTagRoute : HomeGraph()
}

@Serializable
data object DashboardRoute : Route

@Serializable
data object SettingRoute : Route
