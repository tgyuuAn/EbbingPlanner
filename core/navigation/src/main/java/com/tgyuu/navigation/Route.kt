package com.tgyuu.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object OnboardingRoute : Route

@Serializable
data object HomeBaseRoute : Route

sealed interface HomeGraph : Route {
    @Serializable
    data class HomeRoute(val workedDate: String? = null) : HomeGraph

    @Serializable
    data class AddTodoRoute(val selectedDate: String) : HomeGraph

    @Serializable
    data class EditTodoRoute(val scheduleId: Int) : HomeGraph
}

@Serializable
data object DashboardRoute : Route

@Serializable
data object SettingBaseRoute : Route

sealed interface SettingGraph : Route {
    @Serializable
    data object SettingRoute : SettingGraph

    @Serializable
    data class WebViewRoute(val title: String, val url: String) : SettingGraph
}

@Serializable
data object TagBaseRoute : Route

sealed interface TagGraph : Route {
    @Serializable
    data object TagRoute : TagGraph

    @Serializable
    data object AddTagRoute : TagGraph

    @Serializable
    data class EditTagRoute(val tagId: Int) : TagGraph
}
