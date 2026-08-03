package com.tgyuu.shared.common

import kotlin.time.Clock
import kotlin.time.Instant

actual fun currentInstant(): Instant = Clock.System.now()
