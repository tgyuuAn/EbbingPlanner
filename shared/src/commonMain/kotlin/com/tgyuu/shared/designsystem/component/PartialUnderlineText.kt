package com.tgyuu.shared.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * SpanStyle(textDecoration = TextDecoration.Underline) 는 Compose Multiplatform iOS(Skia)에서
 * 해당 span의 baseline 처리가 Android native와 달라 underline이 올라보이는 문제가 있음.
 * onTextLayout으로 정확한 baseline을 측정한 뒤 drawWithContent로 직접 그려 두 플랫폼을 일치시킴.
 */
@Composable
fun EbbingPartialUnderlineText(
    underlinedPart: String,
    rest: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    highlightColor: Color = color,
) {
    var underlineEndX by remember { mutableFloatStateOf(0f) }
    var baselineY by remember { mutableFloatStateOf(0f) }

    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = highlightColor)) { append(underlinedPart) }
            append(rest)
        },
        style = style.copy(color = color),
        onTextLayout = { layout ->
            if (underlinedPart.isNotEmpty()) {
                underlineEndX = layout.getBoundingBox(underlinedPart.length - 1).right
                baselineY = layout.getLineBaseline(0)
            }
        },
        modifier = modifier.drawWithContent {
            drawContent()
            if (underlineEndX > 0f && baselineY > 0f) {
                val strokeWidth = 1.5.dp.toPx()
                val y = baselineY + 2.dp.toPx()
                drawLine(
                    color = highlightColor,
                    start = Offset(0f, y),
                    end = Offset(underlineEndX, y),
                    strokeWidth = strokeWidth,
                )
            }
        },
    )
}
