package com.tgyuu.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingCheck(
    checked: Boolean,
    colorValue: Int,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by animateColorAsState(targetValue = if (checked) Color(colorValue) else EbbingTheme.colors.white)

    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 2.dp,
            color = Color(colorValue),
        ),
        color = buttonColor,
        modifier = modifier.clickable { onCheckedChange(!checked) },
    ) {
        Image(
            painter = painterResource(R.drawable.ic_check),
            colorFilter = ColorFilter.tint(EbbingTheme.colors.white),
            contentDescription = null,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewCheck() {
    BasePreview {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EbbingCheck(
                checked = true,
                colorValue = 0xFF0F4C75.toInt(),
                onCheckedChange = {},
                modifier = Modifier.size(40.dp),
            )

            EbbingCheck(
                checked = false,
                colorValue = 0xFF0F4C75.toInt(),
                onCheckedChange = {},
                modifier = Modifier.size(40.dp),
            )
        }
    }
}
