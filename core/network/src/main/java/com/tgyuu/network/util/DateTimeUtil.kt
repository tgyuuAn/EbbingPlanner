package com.tgyuu.network.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun String.toLocalDateTimeFromUtc(): LocalDateTime =
    ZonedDateTime.parse(this, ISO_OFFSET)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()

fun LocalDateTime.toUtcIsoString(): String =
    this.atZone(ZoneId.systemDefault())
        .withZoneSameInstant(ZoneId.of("UTC"))
        .format(ISO_OFFSET)
