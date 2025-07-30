package com.tgyuu.ebbingplanner.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.tgyuu.dashboard.navigation.scheduleNavigation
import com.tgyuu.ebbingplanner.ui.EbbingAppState
import com.tgyuu.home.navigation.homeGraph
import com.tgyuu.memo.navigation.memoGraph
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.onboarding.navigation.onboardingNavigation
import com.tgyuu.repeatcycle.navigation.repeatCycleGraph
import com.tgyuu.setting.navigation.settingGraph
import com.tgyuu.sync.navigation.syncNavigation
import com.tgyuu.tag.navigation.tagGraph

@Composable
fun AppNavHost(
    appState: EbbingAppState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = appState.navController,
        startDestination = HomeBaseRoute,
        modifier = modifier,
    ) {
        onboardingNavigation()
        homeGraph()
        memoGraph()
        scheduleNavigation()
        settingGraph()
        tagGraph()
        repeatCycleGraph()
        syncNavigation()
    }
}
