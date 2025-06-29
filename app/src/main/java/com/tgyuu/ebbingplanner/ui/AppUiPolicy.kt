package com.tgyuu.ebbingplanner.ui

import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.MemoGraph
import com.tgyuu.navigation.OnboardingRoute
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.SyncGraph
import com.tgyuu.navigation.TagGraph

object AppUiPolicy {
    val bottomBarHiddenRoutes = setOf(
        OnboardingRoute::class,
        HomeGraph.AddTodoRoute::class,
        HomeGraph.EditTodoRoute::class,
        HomeGraph.EditDateRoute::class,
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
        SyncGraph.ConnectRoute::class,
    )

    val rootRoutes = setOf(
        OnboardingRoute::class,
        HomeGraph.HomeRoute::class,
    )

    val networkRequiredRoutes = setOf(
        SyncGraph.SyncMainRoute::class,
        SyncGraph.ConnectRoute::class,
    )
}
