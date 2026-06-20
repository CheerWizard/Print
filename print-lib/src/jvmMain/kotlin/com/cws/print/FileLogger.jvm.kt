package com.cws.print

import java.io.File

actual class FileLogger(
    private val filepath: String
) : Logger {

    private var file: File? = null

    actual override fun open() {
        file = File(filepath)
        file?.let { f ->
            f.parentFile.mkdirs()
            if (!f.exists()) {
                f.createNewFile()
            }
        }
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
        file?.appendText(formatLog(logLevel, tag, message, exception) + "\n")
    }

}