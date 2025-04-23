package com.tgyuu.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme

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
            contentColor = EbbingTheme.colors.white,
            disabledContainerColor = EbbingTheme.colors.light1,
            disabledContentColor = EbbingTheme.colors.white,
        ),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
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

@Preview
@Composable
fun PreviewPieceSolidButton() {
    BasePreview {
        EbbingSolidButton(
            label = "Label",
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
