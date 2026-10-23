package com.tgyuu.shared.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.model.ClickableText

/**
 * Android core/designsystem 의 EbbingClickableText 대응.
 * 본문 안의 URL 을 primary 색 + 밑줄로 강조하고, 탭하면 브라우저로 연다.
 * 링크 클릭은 LinkAnnotation 이 처리하므로 플랫폼별 분기가 필요하지 않다.
 */
@Composable
fun EbbingClickableText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val linkColor = EbbingTheme.colors.primaryDefault
    val clickableText = remember(text) { ClickableText.from(text) }
    val annotatedString = remember(clickableText, linkColor) {
        clickableText.toAnnotatedString(linkColor)
    }

    Text(
        text = annotatedString,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = onTextLayout,
        modifier = modifier,
    )
}
