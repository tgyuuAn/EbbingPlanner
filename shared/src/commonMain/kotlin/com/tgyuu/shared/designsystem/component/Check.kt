package com.tgyuu.shared.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EbbingCheck(
    checked: Boolean,
    colorValue: Int,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by animateColorAsState(
        targetValue = if (checked) Color(colorValue) else EbbingTheme.colors.background,
        label = "check_color",
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = Color(colorValue),
        ),
        color = buttonColor,
        modifier = modifier.clickable { onCheckedChange(!checked) },
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            tint = EbbingTheme.colors.background,
            contentDescription = null,
            modifier = Modifier.padding(8.dp),
        )
    }
}
