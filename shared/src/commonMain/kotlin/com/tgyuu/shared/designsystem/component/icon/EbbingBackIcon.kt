package com.tgyuu.shared.designsystem.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * 상단바 뒤로가기 아이콘 — Android develop 의 `ic_arrow_left`(얇은 쉐브론 "<")와 동일.
 * stroke 기반 32x32 vector.
 */
val EbbingBackIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ArrowLeft",
        defaultWidth = 32.dp,
        defaultHeight = 32.dp,
        viewportWidth = 32f,
        viewportHeight = 32f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString("M21.333,6.4L11.733,16L21.333,25.6").toNodes(),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }.build()
}
