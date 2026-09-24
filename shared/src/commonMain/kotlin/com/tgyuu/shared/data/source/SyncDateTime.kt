package com.tgyuu.shared.data.source

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/** UTC ISO 문자열(예: "2026-06-20T05:30:45Z") → 시스템 로컬 LocalDateTime */
internal fun String.toLocalDateTimeFromUtc(): LocalDateTime =
    Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())

/** 로컬 LocalDateTime → UTC ISO 문자열 */
internal fun LocalDateTime.toUtcIsoString(): String =
    this.toInstant(TimeZone.currentSystemDefault()).toString()
