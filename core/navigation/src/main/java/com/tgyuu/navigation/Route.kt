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
data object MemoBaseRoute : Route

sealed interface MemoGraph : Route {
    @Serializable
    data class AddMemoRoute(val scheduleId: Int? = null) : Route

    @Serializable
    data class EditMemoRoute(val scheduleId: Int? = null) : Route
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

@Serializable
data object RepeatCycleBaseRoute : Route

sealed interface RepeatCycleGraph : Route {
    @Serializable
    data object RepeatCycleRoute : RepeatCycleGraph

    @Serializable
    data object AddRepeatCycleRoute : Route

    @Serializable
    data class EditRepeatCycleRoute(val repeatCycleId: Int? = null) : Route
}

@Serializable
data object SyncBaseRoute : Route

sealed interface SyncGraph : Route {

    @Serializable
    data object SyncMainRoute : SyncGraph

    @Serializable
    data object UploadRoute : SyncGraph

    @Serializable
    data object DownloadRoute : SyncGraph

    @Serializable
    data object LinkRoute : SyncGraph
}
