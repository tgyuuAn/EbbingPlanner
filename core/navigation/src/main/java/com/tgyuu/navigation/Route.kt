package com.tgyuu.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object HomeRoute : Route

@Serializable
data object DashboardRoute : Route

@Serializable
data object SettingRoute : Route
