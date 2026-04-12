package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults.filterChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EbbingChip(
    label: String,
    selected: Boolean,
    onChipClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        label = {
            Text(
                text = label,
                style = if (selected) EbbingTheme.typography.bodyMSB
                else EbbingTheme.typography.bodyMM,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
        },
        selected = selected,
        border = null,
        colors = filterChipColors(
            selectedLabelColor = EbbingTheme.colors.primaryDefault,
            selectedContainerColor = EbbingTheme.colors.primaryLight,
            containerColor = EbbingTheme.colors.light3,
            labelColor = if (selected) EbbingTheme.colors.primaryDefault
            else EbbingTheme.colors.black,
        ),
        onClick = onChipClicked,
        modifier = modifier.height(52.dp),
    )
}
