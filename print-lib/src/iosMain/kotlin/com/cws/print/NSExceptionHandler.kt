@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import platform.Foundation.*

object NSExceptionHandler {

    private val reportWriter = NativeReportWriter()

    fun install(filepath: String) {
        reportWriter.install(filepath)
        NSSetUncaughtExceptionHandler(staticCFunction { exception ->
            exception ?: return@staticCFunction
            val name = exception.name
            val reason = exception.reason
            val stack = exception.callStackSymbols.joinToString("\n")
            reportWriter.write("Caught NSException: $name\nReason: $reason\nStack: $stack".encodeToByteArray())
        })
    }

}