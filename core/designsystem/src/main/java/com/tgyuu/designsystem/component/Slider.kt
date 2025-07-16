package com.tgyuu.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun HorizontalSlider(
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
