package com.tgyuu.shared.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_check
import org.jetbrains.compose.resources.painterResource

@Composable
fun EbbingCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(6.dp)
    val boxColor by animateColorAsState(
        targetValue = if (checked) EbbingTheme.colors.primaryDefault else EbbingTheme.colors.background,
        label = "checkbox_color",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(shape)
            .background(boxColor)
            .then(
                if (checked) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = EbbingTheme.colors.light1,
                        shape = shape,
                    )
                }
            )
            .clickable { onCheckedChange(!checked) },
    ) {
        if (checked) {
            Icon(
                painter = painterResource(Res.drawable.ic_check),
                tint = EbbingTheme.colors.background,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp),
            )
        }
    }
}
