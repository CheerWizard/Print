package com.cws.print

import kotlin.time.Duration.Companion.milliseconds

fun getCurrentTimestamp(pattern: String = "dd.MM.YYYY HH:mm:ss"): String {
    return getCurrentTimeMillis().milliseconds.formatDateTime(pattern)
}

fun formatLog(
    logLevel: LogLevel,
    tag: String,
    message: String,
    exception: Throwable? = null,
    timestamp: String = getCurrentTimestamp(),
): String {
    return if (exception == null) {
        "$timestamp $logLevel $tag: $message"
    } else {
        "$timestamp $logLevel $tag: $message\n${exception.stackTraceToString()}"
    }
}
