package com.cws.print

class LogBuffer {

    constructor(
        capacity: Int,
        logs: Array<LogData> = Array(capacity) { LogData() },
    ) {
        this.logs = logs
    }

    var logs: Array<LogData>
        private set

    var logIndex: Int = 0
        private set

    fun add(logLevel: LogLevel, tag: String, message: String, exception: Throwable?) {
        logs[logIndex++].apply {
            this.timestamp = getCurrentTimeMillis()
            this.level = logLevel
            this.tag = tag
            this.message = message
            this.exception = exception
        }
    }

    fun isEmpty(): Boolean = logIndex == 0

    fun isFull(): Boolean = logIndex > logs.lastIndex

    fun clear() {
        logIndex = 0
    }

}