@file:OptIn(ExperimentalComposeUiApi::class)

package com.cws.print.sandbox

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun main() {
    document.addEventListener("DOMContentLoaded", {
        App()
    })
}

fun App() {
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    Print.install(
        loggers = setOf(
            ConsoleLogger(),
            FileLogger("print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}"),
            UILogger,
            // TODO need to find a host for SigNoz Cloud
//            SignozLogger(host = "")
        ),
    ) {
        appScope.launch {
            PrintTests.run(period = 1.seconds)
        }

        ComposeViewport(document.body!!) {
            UILoggerView()
        }
    }
}
