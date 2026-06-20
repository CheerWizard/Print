package com.cws.print

import android.util.Log

fun LogLevel.toAndroidLogLevel(): Int {
    return when (this) {
        LogLevel.NONE -> 0
        LogLevel.VERBOSE -> Log.VERBOSE
        LogLevel.DEBUG -> Log.DEBUG
        LogLevel.INFO -> Log.INFO
        LogLevel.WARNING -> Log.WARN
        LogLevel.ERROR -> Log.ERROR
        LogLevel.FATAL -> Log.ASSERT
    }
}