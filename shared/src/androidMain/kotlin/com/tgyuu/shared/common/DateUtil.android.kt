package com.tgyuu.shared.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun currentInstant(): Instant = Clock.System.now()
