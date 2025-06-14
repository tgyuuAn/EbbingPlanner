package com.tgyuu.sync.graph.main.ui.dialog

import androidx.compose.ui.text.AnnotatedString

enum class DialogType {
    UPLOAD, DOWNLOAD
}

data class DialogTextModel(
    val title: String,
    val subText: AnnotatedString,
    val buttonText: String
)
