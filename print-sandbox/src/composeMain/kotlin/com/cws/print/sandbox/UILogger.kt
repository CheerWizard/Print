package com.cws.print.sandbox

import androidx.compose.runtime.mutableStateListOf
import com.cws.print.LogLevel
import com.cws.print.Logger
import com.cws.print.formatLog
import com.cws.print.getCurrentTimestamp

data class LogState(
    val timestamp: String,
    val logLevel: LogLevel,
    val tag: String,
    val message: String,
    val exception: Throwable?,
) {
    override fun toString(): String {
        return formatLog(timestamp = timestamp, logLevel = logLevel, tag = tag, message = message, exception = exception)
    }
}

object UILogger : Logger {

    val logs = mutableStateListOf<LogState>()

    override fun open() {
        // no-op
    }

    override fun close() {
        // no-op
    }

    override fun log(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        if (logs.size > 100) {
            logs.removeRange(0, 9)
        }

        logs.add(
            LogState(
                timestamp = getCurrentTimestamp(),
                logLevel = logLevel,
                tag = tag,
                message = message,
                exception = exception
            )
        )
    }

}
