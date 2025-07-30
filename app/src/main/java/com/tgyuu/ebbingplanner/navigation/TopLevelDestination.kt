package com.tgyuu.ebbingplanner.navigation

import androidx.annotation.DrawableRes
import com.tgyuu.ebbingplanner.R
import com.tgyuu.navigation.ScheduleRoute
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
    SCHEDULE(
        iconDrawableId = R.drawable.ic_schedule,
        contentDescription = "모아보기",
        title = "모아보기",
        route = ScheduleRoute::class,
    ),
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
