package com.cws.print

enum class LogLevel {
    NONE,
    VERBOSE,
    INFO,
    DEBUG,
    WARNING,
    ERROR,
    FATAL;

    override fun toString(): String {
        return when (this) {
            NONE -> ""
            VERBOSE -> "[VERBOSE]"
            DEBUG -> "[DEBUG]"
            INFO -> "[INFO]"
            WARNING -> "[WARNING]"
            ERROR -> "[ERROR]"
            FATAL -> "[FATAL]"
        }
    }

    fun toColorCode(): String {
        return when (this) {
            NONE, VERBOSE -> "\u001B[0m"
            DEBUG, INFO -> "\u001B[32m"
            WARNING -> "\u001B[33m"
            ERROR, FATAL -> "\u001B[31m"
        }
    }
}

fun Int.toLogLevel(): LogLevel = when (this) {
    LogLevel.NONE.ordinal -> LogLevel.NONE
    LogLevel.VERBOSE.ordinal -> LogLevel.VERBOSE
    LogLevel.INFO.ordinal -> LogLevel.INFO
    LogLevel.DEBUG.ordinal -> LogLevel.DEBUG
    LogLevel.WARNING.ordinal -> LogLevel.WARNING
    LogLevel.ERROR.ordinal -> LogLevel.ERROR
    LogLevel.FATAL.ordinal -> LogLevel.FATAL
    else -> LogLevel.NONE
}