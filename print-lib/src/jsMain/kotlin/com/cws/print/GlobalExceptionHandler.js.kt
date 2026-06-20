package com.cws.print

import kotlinx.browser.window

actual fun GlobalExceptionHandler(context: PrintContext, block: (Throwable) -> Unit) {
    window.onerror = { msg, url, lineNo, colNo, error ->
        val throwable = error as? Throwable ?: RuntimeException("$msg at $url:$lineNo")
        block(throwable)
        true
    }

    window.onunhandledrejection = { event ->
        val throwable = event.reason as? Throwable ?: RuntimeException(event.reason.toString())
        block(throwable)
    }
}