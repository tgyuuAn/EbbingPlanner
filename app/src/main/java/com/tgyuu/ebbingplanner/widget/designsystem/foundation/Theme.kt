package com.tgyuu.ebbingplanner.widget.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider
import androidx.glance.color.colorProviders
import com.tgyuu.designsystem.foundation.forestDarkColorScheme
import com.tgyuu.designsystem.foundation.forestLightColorScheme
import com.tgyuu.designsystem.foundation.lilacDarkColorScheme
import com.tgyuu.designsystem.foundation.lilacLightColorScheme
import com.tgyuu.designsystem.foundation.marineDarkColorScheme
import com.tgyuu.designsystem.foundation.marineLightColorScheme
import com.tgyuu.designsystem.foundation.normalDarkColorScheme
import com.tgyuu.designsystem.foundation.normalLightColorScheme
import com.tgyuu.designsystem.foundation.sunsetDarkColorScheme
import com.tgyuu.designsystem.foundation.sunsetLightColorScheme
import com.tgyuu.domain.model.Theme

@Composable
fun EbbingWidgetTheme(
    theme: Theme = Theme.NORMAL,
    alpha: Float = 1f,
    content: @Composable () -> Unit,
) {
    val light = when (theme) {
        Theme.NORMAL -> normalLightColorScheme
        Theme.FOREST -> forestLightColorScheme
        Theme.SUNSET -> sunsetLightColorScheme
        Theme.MARINE -> marineLightColorScheme
        Theme.LILAC -> lilacLightColorScheme
    }
    val dark = when (theme) {
        Theme.NORMAL -> normalDarkColorScheme
        Theme.FOREST -> forestDarkColorScheme
        Theme.SUNSET -> sunsetDarkColorScheme
        Theme.MARINE -> marineDarkColorScheme
        Theme.LILAC -> lilacDarkColorScheme
    }

    val providers = colorProviders(
        background = ColorProvider(
            light.background.copy(alpha = alpha),
            dark.background.copy(alpha = alpha)
        ),
        primary = ColorProvider(
            light.primaryDefault.copy(alpha = alpha),
            dark.primaryDefault.copy(alpha = alpha)
        ),
        primaryContainer = ColorProvider(
            light.light2.copy(alpha = alpha),
            dark.light2.copy(alpha = alpha)
        ),
        surface = ColorProvider(
            light.black.copy(alpha = alpha),
            dark.black.copy(alpha = alpha)
        ),
        inverseSurface = ColorProvider(
            light.white.copy(alpha = alpha),
            dark.white.copy(alpha = alpha)
        ),
        surfaceVariant = ColorProvider(
            light.light1.copy(alpha = alpha),
            dark.light1.copy(alpha = alpha)
        ),
        secondary = ColorProvider(
            light.primaryLight.copy(alpha = alpha),
            dark.primaryLight.copy(alpha = alpha)
        ),
        tertiary = ColorProvider(
            light.dark3.copy(alpha = alpha),
            dark.dark3.copy(alpha = alpha)
        ),

        // 아래는 여전히 사용하지 않는 색상으로 유지
        onPrimary = ColorProvider(Color.Transparent, Color.Transparent),
        onPrimaryContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onSecondary = ColorProvider(Color.Transparent, Color.Transparent),
        secondaryContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onSecondaryContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onTertiary = ColorProvider(Color.Transparent, Color.Transparent),
        tertiaryContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onTertiaryContainer = ColorProvider(Color.Transparent, Color.Transparent),
        error = ColorProvider(Color.Transparent, Color.Transparent),
        errorContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onError = ColorProvider(Color.Transparent, Color.Transparent),
        onErrorContainer = ColorProvider(Color.Transparent, Color.Transparent),
        onBackground = ColorProvider(Color.Transparent, Color.Transparent),
        inverseOnSurface = ColorProvider(Color.Transparent, Color.Transparent),
        onSurface = ColorProvider(Color.Transparent, Color.Transparent),
        onSurfaceVariant = ColorProvider(Color.Transparent, Color.Transparent),
        outline = ColorProvider(Color.Transparent, Color.Transparent),
        inversePrimary = ColorProvider(Color.Transparent, Color.Transparent),
        widgetBackground = ColorProvider(Color.Transparent, Color.Transparent),
    )

    GlanceTheme(
        colors = providers,
        content = content
    )
}

internal val THEME = stringPreferencesKey("theme")
internal val ALPHA = floatPreferencesKey("alpha")
