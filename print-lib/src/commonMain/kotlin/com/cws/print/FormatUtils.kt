package com.cws.print

import kotlin.time.Duration.Companion.milliseconds

fun getCurrentTimestamp(): String {
    return getCurrentTimeMillis().milliseconds.formatDateTime("dd.MM.YYYY HH:mm:ss")
}

fun formatLog(logLevel: LogLevel, tag: String, message: String, exception: Throwable? = null): String {
    return if (exception == null) {
        "${getCurrentTimestamp()} $logLevel $tag: $message"
    } else {
        "${getCurrentTimestamp()} $logLevel $tag: $message\n${exception.stackTraceToString()}"
    }
}
