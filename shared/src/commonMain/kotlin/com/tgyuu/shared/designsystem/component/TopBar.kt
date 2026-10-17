package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.tgyuu.shared.designsystem.component.icon.EbbingBackIcon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun EbbingMainTopBar(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = EbbingTheme.colors.black,
    rightComponent: @Composable () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        Text(
            text = title,
            style = EbbingTheme.typography.headingMSB,
            color = titleColor,
        )

        Spacer(modifier = Modifier.weight(1f))

        rightComponent()
    }
}

@Composable
fun EbbingSubTopBar(
    title: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    rightComponent: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        Icon(
            imageVector = EbbingBackIcon,
            contentDescription = "Back",
            tint = EbbingTheme.colors.black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(32.dp)
                .clickable { onNavigationClick() },
        )

        Text(
            text = title,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.align(Alignment.Center),
        )

        rightComponent()
    }
}
