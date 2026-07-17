@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import com.cws.print.LogLevel
import com.cws.print.getCurrentTimeMillis
import com.cws.print.getCurrentTimestamp
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

external interface IDBLogEntry : JsAny {
    var timestamp: Double
    var dateTime: String
    var logLevel: String
    var tag: String
    var message: String
    var errorStack: Array<String>?
}

fun IDBLogEntry(
    timestamp: Double = getCurrentTimeMillis().toDouble(),
    dateTime: String = getCurrentTimestamp(),
    logLevel: LogLevel,
    tag: String,
    message: String,
    exception: Throwable? = null,
): IDBLogEntry {
    val log: IDBLogEntry = js("({})")
    log.timestamp = timestamp
    log.dateTime = dateTime
    log.logLevel = logLevel.name
    log.tag = tag
    log.message = message
    log.errorStack = exception.toStackFrames()
    return log
}

fun Throwable?.toStackFrames(): Array<String>? {
    if (this == null) return null
    val rawStack = stackTraceToString()
    return rawStack
        .split("\n")
        .filter { it.isNotBlank() }
        .toTypedArray()
}
