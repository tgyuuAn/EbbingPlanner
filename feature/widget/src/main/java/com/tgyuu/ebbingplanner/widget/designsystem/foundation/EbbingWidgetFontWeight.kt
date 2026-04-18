package com.tgyuu.ebbingplanner.widget.designsystem.foundation

import androidx.glance.text.FontWeight
import kotlin.reflect.jvm.isAccessible

val FontWeight.Companion.SemiBold: FontWeight
    get() = FontWeight::class.constructors
        .first()
        .apply { isAccessible = true }
        .call(600)
