package com.cws.print

interface Logger {
    fun open()
    fun close()
    fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable? = null)
}