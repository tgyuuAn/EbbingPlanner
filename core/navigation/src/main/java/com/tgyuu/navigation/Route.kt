package com.tgyuu.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object HomeRoute : Route

@Serializable
data object DashBoardRoute : Route

@Serializable
data object SettingRoute : Route
