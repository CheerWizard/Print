package com.cws.print

data class LogData(
    var timestamp: Long = 0,
    var level: LogLevel = LogLevel.NONE,
    var tag: String = "",
    var message: String = "",
    var exception: Throwable? = null
)
