package com.tgyuu.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults.filterChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.foundation.EbbingTheme

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
                style = if (selected) EbbingTheme.typography.heading16SB
                else EbbingTheme.typography.body16M,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
        },
        selected = selected,
        border = null,
        colors = filterChipColors(
            selectedLabelColor = EbbingTheme.colors.textOnPrimary,
            selectedContainerColor = EbbingTheme.colors.fillPrimary,
            containerColor = EbbingTheme.colors.fillTextfield,
            labelColor = if (selected) EbbingTheme.colors.textOnPrimary
            else EbbingTheme.colors.textSub,
        ),
        onClick = onChipClicked,
        modifier = modifier.height(52.dp),
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewChip() {
    BasePreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EbbingChip(
                label = "Non-Selected",
                selected = false,
                onChipClicked = {},
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp),
            )

            EbbingChip(
                label = "Selected",
                selected = true,
                onChipClicked = {},
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp),
            )
        }
    }
}
