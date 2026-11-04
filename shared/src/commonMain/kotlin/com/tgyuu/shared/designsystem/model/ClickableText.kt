package com.tgyuu.shared.designsystem.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

/**
 * Android core/designsystem 의 ClickableText 대응.
 * 본문에서 URL 을 찾아 링크 스타일과 클릭 동작을 붙인다.
 */
data class ClickableText(
    val originalText: String,
    private val urlPositions: List<UrlPosition>,
) {
    data class UrlPosition(
        val start: Int,
        val end: Int,
        val url: String,
    )

    companion object {
        private val URL_REGEX = "(https?://[^\\s]+|www\\.[^\\s]+)".toRegex()

        fun from(text: String): ClickableText {
            val positions = URL_REGEX.findAll(text).map { matchResult ->
                UrlPosition(
                    start = matchResult.range.first,
                    end = matchResult.range.last + 1,
                    url = matchResult.value,
                )
            }.toList()

            return ClickableText(text, positions)
        }
    }

    fun toAnnotatedString(linkColor: Color): AnnotatedString {
        return buildAnnotatedString {
            append(originalText)

            urlPositions.forEach { position ->
                // Android 는 "www." 로 시작하면 https 를 앞에 붙여 연다.
                val url = if (position.url.startsWith("www.")) {
                    "https://${position.url}"
                } else {
                    position.url
                }

                addLink(
                    url = LinkAnnotation.Url(
                        url = url,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ),
                    ),
                    start = position.start,
                    end = position.end,
                )
            }
        }
    }
}
