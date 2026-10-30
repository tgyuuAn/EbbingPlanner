package com.tgyuu.shared.designsystem.component.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * 동기화 버튼 아이콘 — Android develop 의 `ic_link`(체인/링크 모양)와 동일.
 * material-icons-extended 의존성 없이 vector path 로 정의.
 */
val EbbingSyncIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Link",
        defaultWidth = 16.dp,
        defaultHeight = 16.dp,
        viewportWidth = 16f,
        viewportHeight = 16f,
    ).apply {
        addPath(
            pathData = PathParser().parsePathString(
                "M4,9h1v1L4,10c-1.5,0 -3,-1.69 -3,-3.5S2.55,3 4,3h4c1.45,0 3,1.69 3,3.5 " +
                    "0,1.41 -0.91,2.72 -2,3.25L9,8.59c0.58,-0.45 1,-1.27 1,-2.09C10,5.22 8.98,4 8,4L4,4" +
                    "c-0.98,0 -2,1.22 -2,2.5S3,9 4,9zM13,6h-1v1h1c1,0 2,1.22 2,2.5S13.98,12 13,12L9,12" +
                    "c-0.98,0 -2,-1.22 -2,-2.5 0,-0.83 0.42,-1.64 1,-2.09L8,6.25c-1.09,0.53 -2,1.84 -2,3.25" +
                    "C6,11.31 7.55,13 9,13h4c1.45,0 3,-1.69 3,-3.5S14.5,6 13,6z",
            ).toNodes(),
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.EvenOdd,
        )
    }.build()
}
