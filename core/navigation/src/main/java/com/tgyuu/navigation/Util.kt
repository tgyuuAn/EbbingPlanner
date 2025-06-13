package com.tgyuu.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
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
    SyncGraph.UploadRoute::class,
    SyncGraph.DownloadRoute::class,
    SyncGraph.LinkRoute::class,
)

private val ROOT_ROUTES = setOf(
    OnboardingRoute::class,
    HomeGraph.HomeRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean = this?.hierarchy?.any { destination ->
    HIDDEN_BOTTOM_BAR_ROUTES.any {
        destination.route?.startsWith(it.qualifiedName ?: "") == true
    }
} ?: false

fun NavDestination?.isRootRoute(): Boolean = this?.hierarchy?.any { destination ->
    ROOT_ROUTES.any { destination.route?.startsWith(it.qualifiedName ?: "") == true }
} ?: false

fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true
