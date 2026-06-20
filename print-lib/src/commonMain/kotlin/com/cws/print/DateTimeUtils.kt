package com.cws.print

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun getCurrentTimeNanos(): Long {
    return Clock.System.now().toEpochMilliseconds() * 1000
}

@OptIn(ExperimentalTime::class)
fun Duration.formatDateTime(pattern: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val instant = Instant.fromEpochMilliseconds(inWholeMilliseconds)
    val localTime = instant.toLocalDateTime(timeZone)
    val date = localTime.date
    val time = localTime.time

    val formatted = pattern
        .replace("YYYY", date.year.toString())
        .replace("MM", date.month.number.toString().padStart(2, '0'))
        .replace("dd", date.day.toString().padStart(2, '0'))
        .replace("HH", time.hour.toString().padStart(2, '0'))
        .replace("mm", time.minute.toString().padStart(2, '0'))
        .replace("ss", time.second.toString().padStart(2, '0'))

    return formatted
}