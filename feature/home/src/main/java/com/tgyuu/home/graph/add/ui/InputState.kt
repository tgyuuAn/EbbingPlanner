package com.tgyuu.home.graph.add.ui

enum class InputState {
    DEFAULT,
    WARNING,
    ;

    companion object {
        fun getBooleanState(fieldValue: Boolean?): InputState =
            if (fieldValue == null) WARNING else DEFAULT
    }
}
