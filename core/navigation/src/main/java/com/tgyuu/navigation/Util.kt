package com.tgyuu.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
    OnboardingRoute::class,
    HomeGraph.AddTodoRoute::class,
    TagGraph.TagRoute::class,
    TagGraph.AddTagRoute::class,
    TagGraph.EditTagRoute::class,
    SettingGraph.WebViewRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean = this?.hierarchy?.any { destination ->
    HIDDEN_BOTTOM_BAR_ROUTES.any {
        destination.route?.startsWith(it.qualifiedName ?: "") == true
    }
} ?: false

fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true
