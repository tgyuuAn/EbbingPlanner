package com.tgyuu.analytics.data.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.tgyuu.analytics.data.NoOpAnalyticsHelper
import com.tgyuu.analytics.domain.AnalyticsHelper
import com.tgyuu.analytics.domain.extractScreenName
import com.tgyuu.analytics.domain.model.AnalyticsEvent
import com.tgyuu.analytics.domain.model.AnalyticsEvent.PropertiesKeys.BUTTON_NAME
import com.tgyuu.analytics.domain.model.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.tgyuu.analytics.domain.model.AnalyticsEvent.Types.BUTTON_CLICK
import com.tgyuu.analytics.domain.model.AnalyticsEvent.Types.SCREEN_VIEW

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
