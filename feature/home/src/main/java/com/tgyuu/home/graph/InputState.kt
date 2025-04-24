package com.tgyuu.home.graph

enum class InputState {
    DEFAULT,
    WARNING,
    ;

    companion object {
        fun getStringInputState(fieldValue: String?): InputState =
            if (fieldValue?.trim().isNullOrBlank()) WARNING else DEFAULT
    }
}
