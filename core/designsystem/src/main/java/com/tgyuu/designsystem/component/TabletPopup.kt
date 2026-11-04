package com.tgyuu.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme

/**
 * 태블릿에서 화면 위에 한 단계 더 띄우는 폼 팝업(예: 드롭다운의 "새 태그 추가").
 * 다이얼로그 창 안에서 호출해요. 창의 80% 크기(최대 760×1120dp)로 떠요.
 */
@Composable
fun EbbingTabletFormPopup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)

    BoxWithConstraints(
        modifier = modifier
            .imePadding()
            .padding(20.dp),
    ) {
        // 디자인: 960×1440 화면에서 760×1120 (약 80%). 큰 화면에서는 최대 크기에서 멈춰요
        val popupWidth = minOf(maxWidth * 0.8f, 760.dp)
        val popupHeight = minOf(maxHeight * 0.8f, 1120.dp)

        Column(
            modifier = Modifier
                .size(width = popupWidth, height = popupHeight)
                .shadow(elevation = 8.dp, shape = shape)
                .clip(shape)
                .background(EbbingTheme.colors.background)
                .padding(32.dp),
            content = content,
        )
    }
}
