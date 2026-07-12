package com.tgyuu.ebbingplanner.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.tgyuu.ebbingplanner.R
import com.tgyuu.designsystem.R as DesignR
import com.tgyuu.navigation.ScheduleRoute
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.SettingGraph.SettingRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    @DrawableRes val iconDrawableId: Int,
    @StringRes val contentDescriptionRes: Int,
    @StringRes val titleRes: Int,
    val route: KClass<*>,
) {
    HOME(
        iconDrawableId = R.drawable.ic_home,
        contentDescriptionRes = DesignR.string.nav_home,
        titleRes = DesignR.string.nav_home,
        route = HomeRoute::class,
    ),
    SCHEDULE(
        iconDrawableId = R.drawable.ic_schedule,
        contentDescriptionRes = DesignR.string.nav_schedule,
        titleRes = DesignR.string.nav_schedule,
        route = ScheduleRoute::class,
    ),
    SETTING(
        iconDrawableId = R.drawable.ic_setting,
        contentDescriptionRes = DesignR.string.nav_setting,
        titleRes = DesignR.string.nav_setting,
        route = SettingRoute::class,
    );

    companion object {
        val topLevelDestinations = entries
    }
}
