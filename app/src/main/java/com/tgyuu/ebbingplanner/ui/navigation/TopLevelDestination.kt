package com.tgyuu.ebbingplanner.ui.navigation

import androidx.annotation.DrawableRes
import com.tgyuu.ebbingplanner.R
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.SettingGraph.SettingRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    @DrawableRes val iconDrawableId: Int,
    val contentDescription: String,
    val title: String,
    val route: KClass<*>,
) {
    HOME(
        iconDrawableId = R.drawable.ic_home,
        contentDescription = "홈",
        title = "홈",
        route = HomeRoute::class,
    ),

    //    DASHBOARD(
//        iconDrawableId = R.drawable.ic_dashboard,
//        contentDescription = "대시보드",
//        title = "대시보드",
//        route = DashboardRoute::class,
//    ),
    SETTING(
        iconDrawableId = R.drawable.ic_setting,
        contentDescription = "설정",
        title = "설정",
        route = SettingRoute::class,
    );

    companion object {
        val topLevelDestinations = entries
    }
}
