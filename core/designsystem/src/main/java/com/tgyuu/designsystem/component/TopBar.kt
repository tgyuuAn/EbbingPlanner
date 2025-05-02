package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

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
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "뒤로 가기 버튼",
            colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onNavigationClick() }
        )

        Text(
            text = title,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.align(Alignment.Center)
        )

        rightComponent()
    }
}

@EbbingPreview
@Composable
fun PreviewTopBar() {
    BasePreview {
        EbbingSubTopBar(
            title = "Page Name",
            onNavigationClick = { },
            rightComponent = {
                Image(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "오른쪽 버튼",
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp),
                )
            },
            modifier = Modifier
                .padding(vertical = 20.dp)
                .background(EbbingTheme.colors.white),
        )
    }
}
