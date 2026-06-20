package com.cws.print

expect class FileLogger : Logger {
    override fun open()
    override fun close()
    override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    )
}