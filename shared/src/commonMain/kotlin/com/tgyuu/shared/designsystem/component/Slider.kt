package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import kotlin.math.abs

@Composable
fun HorizontalBackgroundSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val allowedValues = listOf(0.25f, 0.5f, 0.75f, 1f)

    Slider(
        value = value,
        valueRange = 0.25f..1f,
        onValueChange = { userValue ->
            val snapped = allowedValues.minByOrNull { abs(it - userValue) } ?: userValue
            onValueChange(snapped)
        },
        steps = 0,
        colors = SliderDefaults.colors(
            thumbColor = EbbingTheme.colors.primaryDefault,
            activeTrackColor = EbbingTheme.colors.primaryDefault,
            activeTickColor = EbbingTheme.colors.primaryDefault,
            inactiveTrackColor = EbbingTheme.colors.light1,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}

@Composable
fun HorizontalTextSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        value = value,
        valueRange = 0.1f..1f,
        onValueChange = { onValueChange(it) },
        colors = SliderDefaults.colors(
            thumbColor = EbbingTheme.colors.primaryDefault,
            activeTrackColor = EbbingTheme.colors.primaryDefault,
            activeTickColor = EbbingTheme.colors.primaryDefault,
            inactiveTrackColor = EbbingTheme.colors.light1,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}
