package com.cws.print

import android.content.Context
import java.io.File

actual class FileLogger(
    context: Context,
    private val filepath: String
) : Logger {

    private var file: File? = null
    private val internalDir: File = context.filesDir

    actual override fun open() {
        file = File(internalDir, filepath)
    }

    actual override fun close() {
        file = null
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        file?.writeText(formatLog(logLevel, tag, message, exception))
    }

}