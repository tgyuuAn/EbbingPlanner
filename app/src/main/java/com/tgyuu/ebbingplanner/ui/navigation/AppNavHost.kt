package com.tgyuu.ebbingplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.tgyuu.home.navigation.homeGraph
import com.tgyuu.memo.navigation.memoGraph
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.onboarding.navigation.onboardingNavigation
import com.tgyuu.repeatcycle.navigation.repeatCycleGraph
import com.tgyuu.setting.navigation.settingGraph
import com.tgyuu.tag.navigation.tagGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier,
    ) {
        onboardingNavigation()
        homeGraph()
        memoGraph()
//        dashboardNavigation()
        settingGraph()
        tagGraph()
        repeatCycleGraph()
    }
}
