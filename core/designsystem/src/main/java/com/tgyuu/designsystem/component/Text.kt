package com.tgyuu.designsystem.component

import android.content.Intent
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.net.toUri
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.designsystem.model.ClickableText as ClickableTextModel

@Composable
fun EbbingClickableText(
    clickableText: ClickableTextModel,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val context = LocalContext.current
    val linkColor = EbbingTheme.colors.primaryNormal
    val annotatedString = clickableText.toAnnotatedString(linkColor)

    ClickableText(
        text = annotatedString,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
        onTextLayout = onTextLayout,
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = "URL",
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                val url = if (annotation.item.startsWith("www.")) {
                    "https://${annotation.item}"
                } else {
                    annotation.item
                }
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    )
}
