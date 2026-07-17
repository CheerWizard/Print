@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

internal fun consoleInfo(message: String): Unit = js("console.info(message)")

internal fun consoleWarn(message: String): Unit = js("console.warn(message)")

internal fun consoleError(message: String): Unit = js("console.error(message)")

internal fun consoleError(message: String, errorStack: JsAny?): Unit = js("console.error(message, errorStack)")

actual class ConsoleLogger actual constructor() : Logger {

    actual override fun open() {
        // do nothing
    }

    actual override fun close() {
        // do nothing
    }

    actual override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        val formattedMessage = formatLog(logLevel, tag, message)

        if (logLevel.ordinal >= LogLevel.ERROR.ordinal) {
            consoleError(formattedMessage)
        } else if (logLevel.ordinal == LogLevel.WARNING.ordinal) {
            consoleWarn(formattedMessage)
        } else {
            consoleInfo(formattedMessage)
        }

        exception?.printStackTrace()
    }

}
