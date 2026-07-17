@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print

import kotlinx.browser.window
import kotlin.js.ExperimentalWasmJsInterop

actual fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit) {
    window.onerror = { msg, url, lineNo, colNo, error ->
        val throwable = error as? Throwable ?: RuntimeException("$msg at $url:$lineNo:$colNo")
        block(throwable)
        error
    }

    window.onunhandledrejection = { event ->
        val throwable = event.reason as? Throwable ?: RuntimeException(event.reason.toString())
        block(throwable)
    }
}
