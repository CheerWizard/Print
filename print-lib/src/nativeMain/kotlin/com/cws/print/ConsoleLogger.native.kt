package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fflush
import platform.posix.fprintf
import platform.posix.stderr
import platform.posix.stdout

@OptIn(ExperimentalForeignApi::class)
actual class ConsoleLogger actual constructor() : Logger {

    actual override fun open() {
        // do nothing
    }

    actual override fun close() {
        // do nothing
    }

    actual override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        val formattedMessage = "${logLevel.toColorCode()}${formatLog(logLevel, tag, message)}"

        if (logLevel.ordinal >= LogLevel.ERROR.ordinal) {
            fprintf(stderr, formattedMessage)
        } else {
            fprintf(stdout, formattedMessage)
            fflush(stdout)
        }

        exception?.printStackTrace()
    }

}