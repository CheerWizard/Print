package com.cws.print

import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual class FileLogger(
    private val filepath: String,
    private val flushPeriod: Duration = 3.seconds,
) : Logger {

    private var jvmFileLogger: JVMFileLogger? = null

    actual override fun open() {
        if (jvmFileLogger != null) return

        jvmFileLogger = JVMFileLogger(
            File(filepath),
            flushPeriod
        ).apply {
            open()
        }
    }

    actual override fun close() {
        jvmFileLogger?.close()
        jvmFileLogger = null
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        jvmFileLogger?.log(logLevel, tag, message, exception)
    }

}