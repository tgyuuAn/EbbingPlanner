package com.tgyuu.common

import java.io.IOException

fun Throwable.isNetworkError(): Boolean =
    generateSequence(this) { it.cause }.any { it is IOException }
