package com.cws.print

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fflush
import platform.posix.fopen
import platform.posix.fwrite

@OptIn(ExperimentalForeignApi::class)
actual class FileLogger(
    private val filepath: String
) : Logger {

    private var file: CPointer<FILE>? = null

    actual override fun open() {
        file = fopen(filepath, "w")
    }

    actual override fun close() {
        if (file != null) {
            fclose(file)
        }
        file = null
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        if (file != null) {
            memScoped {
                // TODO: can be optimized by caching messages in nativeHeap, to remove need of memScoped arena
                val log = formatLog(logLevel, tag, message, exception)
                fwrite(log.cstr.getPointer(memScope), 1u, log.length.toULong(), file)
                fflush(file)
            }
        }
    }

}