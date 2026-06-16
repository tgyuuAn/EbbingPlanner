package com.tgyuu.common

import java.io.IOException

fun Throwable.isNetworkError(): Boolean = this is IOException
