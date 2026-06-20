package com.cws.print

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
            System.err.println(formattedMessage)
        } else {
            System.out.println("${logLevel.toColorCode()}$formattedMessage")
        }

        exception?.printStackTrace()
    }

}