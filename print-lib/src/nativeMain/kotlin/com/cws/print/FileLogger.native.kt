package com.cws.print

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual class FileLogger(
    private val filepath: String,
    private val flushPeriod: Duration = 3.seconds
) : Logger {

    private var nativeFileLogger: NativeFileLogger? = null

    actual override fun open() {
        if (nativeFileLogger != null) return
        nativeFileLogger = NativeFileLogger(filepath, flushPeriod).apply {
            open()
        }
    }

    actual override fun close() {
        nativeFileLogger?.close()
        nativeFileLogger = null
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        nativeFileLogger?.log(logLevel, tag, message, exception)
    }

}