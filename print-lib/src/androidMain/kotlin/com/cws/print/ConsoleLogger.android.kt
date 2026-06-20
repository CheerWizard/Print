package com.cws.print

import android.util.Log

actual class ConsoleLogger actual constructor() : Logger {

    actual override fun open() {
        // do nothing
    }

    actual override fun close() {
        // do nothing
    }

    actual override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        if (logLevel == LogLevel.NONE) return

        if (exception == null) {
            Log.println(logLevel.toAndroidLogLevel(), tag, message)
        } else {
            Log.wtf(tag, message, exception)
        }
    }

}