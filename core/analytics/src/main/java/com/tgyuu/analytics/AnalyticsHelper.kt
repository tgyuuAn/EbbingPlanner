package com.tgyuu.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.tgyuu.analytics.AnalyticsEvent.PropertiesKeys.BUTTON_NAME
import com.tgyuu.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.tgyuu.analytics.AnalyticsEvent.Types.BUTTON_CLICK
import com.tgyuu.analytics.AnalyticsEvent.Types.SCREEN_VIEW

abstract class AnalyticsHelper {
    abstract fun logEvent(event: AnalyticsEvent)
    abstract fun setUserId(userId: String?)
}

class NoOpAnalyticsHelper : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun setUserId(userId: String?) = Unit
}

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}

@Composable
fun TrackNavigationDestination(navController: NavHostController) {
    val analyticsHelper = LocalAnalyticsHelper.current

    LifecycleStartEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route.orEmpty()
            val screenName = extractScreenName(route)

            analyticsHelper.logEvent(
                AnalyticsEvent(
                    type = SCREEN_VIEW,
                    properties = mutableMapOf(SCREEN_NAME to screenName)
                )
            )
        }

        navController.addOnDestinationChangedListener(listener)
        onStopOrDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

@Composable
fun TrackScreenViewEvent(
    key: Any?,
    screenName: String?,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = LaunchedEffect(key) {
    if (screenName != null) {
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = SCREEN_VIEW,
                properties = mutableMapOf(
                    SCREEN_NAME to screenName,
                ),
            ),
        )
    }
}

@Composable
fun TrackClickEvent(
    key: Any?,
    screenName: String,
    buttonName: String,
    properties: MutableMap<String, Any?>? = null,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = LaunchedEffect(key) {
    val eventProperties = mutableMapOf<String, Any?>(
        SCREEN_NAME to screenName,
        BUTTON_NAME to buttonName,
    )

    properties?.let { eventProperties.putAll(it) }

    analyticsHelper.logEvent(
        AnalyticsEvent(
            type = BUTTON_CLICK,
            properties = eventProperties
        )
    )
}
