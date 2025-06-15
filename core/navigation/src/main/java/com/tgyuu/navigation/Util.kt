package com.tgyuu.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val BOTTOM_BAR_HIDDEN_ROUTES = setOf(
    OnboardingRoute::class,
    HomeGraph.AddTodoRoute::class,
    HomeGraph.EditTodoRoute::class,
    TagGraph.TagRoute::class,
    TagGraph.AddTagRoute::class,
    TagGraph.EditTagRoute::class,
    RepeatCycleGraph.RepeatCycleRoute::class,
    RepeatCycleGraph.AddRepeatCycleRoute::class,
    RepeatCycleGraph.EditRepeatCycleRoute::class,
    SettingGraph.WebViewRoute::class,
    MemoGraph.AddMemoRoute::class,
    MemoGraph.EditMemoRoute::class,
    SyncGraph.SyncMainRoute::class,
    SyncGraph.LinkRoute::class,
)

private val ROOT_ROUTES = setOf(
    OnboardingRoute::class,
    HomeGraph.HomeRoute::class,
)

private val NETWORK_REQUIRED_ROUTES = setOf(
    SyncGraph.SyncMainRoute::class,
    SyncGraph.LinkRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean = this?.hierarchy?.any { destination ->
    BOTTOM_BAR_HIDDEN_ROUTES.any {
        destination.route?.startsWith(it.qualifiedName ?: "") == true
    }
} ?: false

fun NavDestination?.isRootRoute(): Boolean = this?.hierarchy?.any { destination ->
    ROOT_ROUTES.any { destination.route?.startsWith(it.qualifiedName ?: "") == true }
} ?: false

fun NavDestination?.requiresNetworkConnection(): Boolean = this?.hierarchy?.any { destination ->
    NETWORK_REQUIRED_ROUTES.any { destination.route?.startsWith(it.qualifiedName ?: "") == true }
} ?: false

fun NavDestination?.hasRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true
