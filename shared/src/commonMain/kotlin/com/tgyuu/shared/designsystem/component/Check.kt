package com.tgyuu.shared.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_check

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
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = Color(colorValue),
        ),
        color = buttonColor,
        modifier = modifier.clickable { onCheckedChange(!checked) },
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_check),
            tint = EbbingTheme.colors.background,
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        )
    }
}
