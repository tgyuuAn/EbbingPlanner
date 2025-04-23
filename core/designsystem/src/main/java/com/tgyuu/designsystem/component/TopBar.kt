package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingSubTopBar(
    title: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    rightComponent: @Composable () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "뒤로 가기 버튼",
            modifier = Modifier.clickable { onNavigationClick() }
        )

        Text(
            text = title,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
        )

        rightComponent()
    }
}

@Preview
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
                    modifier = Modifier.size(32.dp),
                )
            },
            modifier = Modifier
                .padding(vertical = 20.dp)
                .background(EbbingTheme.colors.white),
        )
    }
}
