package com.tgyuu.designsystem.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

data class ClickableText(
    val originalText: String,
    private val urlPositions: List<UrlPosition>
) {
    data class UrlPosition(
        val start: Int,
        val end: Int,
        val url: String
    )

    companion object {
        private val URL_REGEX = "(https?://[^\\s]+|www\\.[^\\s]+)".toRegex()

        fun from(text: String): ClickableText {
            val positions = URL_REGEX.findAll(text).map { matchResult ->
                UrlPosition(
                    start = matchResult.range.first,
                    end = matchResult.range.last + 1,
                    url = matchResult.value
                )
            }.toList()

            return ClickableText(text, positions)
        }
    }

    fun toAnnotatedString(linkColor: Color): AnnotatedString {
        return buildAnnotatedString {
            append(originalText)

            urlPositions.forEach { position ->
                addStyle(
                    style = SpanStyle(
                        color = linkColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = position.start,
                    end = position.end
                )

                addStringAnnotation(
                    tag = "URL",
                    annotation = position.url,
                    start = position.start,
                    end = position.end
                )
            }
        }
    }
}
