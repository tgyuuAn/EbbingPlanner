package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EbbingSolidButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EbbingTheme.colors.primaryDefault,
            contentColor = EbbingTheme.colors.background,
            disabledContainerColor = EbbingTheme.colors.light1,
            disabledContentColor = EbbingTheme.colors.background,
        ),
        contentPadding = PaddingValues(vertical = 14.dp),
        modifier = modifier
            .height(52.dp)
            .widthIn(min = 100.dp),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.bodyMSB,
        )
    }
}

@Composable
fun EbbingOutlinedButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        border = BorderStroke(width = 1.dp, color = EbbingTheme.colors.primaryDefault),
        colors = ButtonDefaults.buttonColors(
            containerColor = EbbingTheme.colors.background,
            contentColor = EbbingTheme.colors.primaryDefault,
            disabledContainerColor = EbbingTheme.colors.light1,
            disabledContentColor = EbbingTheme.colors.primaryDefault,
        ),
        contentPadding = PaddingValues(vertical = 14.dp),
        modifier = modifier
            .height(52.dp)
            .widthIn(min = 100.dp),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.bodyMSB,
        )
    }
}
