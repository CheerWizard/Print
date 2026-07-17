package com.cws.print

// basically stub class, because web doesn't have true file access
// so logger is delegated to WebStorageLogger types
actual class FileLogger(
    // usually need only 1 storage logger for adaptation
    // in case if you need more, just add logger to Print.install(loggers = ...)
    private val storageLogger: WebStorageLogger,
) : Logger {

    actual override fun open() = storageLogger.open()

    actual override fun close() = storageLogger.close()

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) = storageLogger.log(logLevel, tag, message, exception)

}