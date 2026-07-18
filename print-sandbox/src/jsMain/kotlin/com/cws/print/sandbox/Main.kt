@file:OptIn(ExperimentalComposeUiApi::class)

package com.cws.print.sandbox

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.FirebaseWebStreamLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import com.cws.print.idb.IDBLogger
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
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
            FileLogger(IDBLogger(
                name = "print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}",
                version = 1,
                flushPeriod = 3.seconds,
                maxLogsCount = 5000,
            )),
            UILogger,
//            FirebaseWebStreamLogger(
//                measurementId = "G-YMCLLHX18F",
//                apiSecret = "-FjHofacQOKZ_jWD-uSD1g",
//                clientId = "test-client-1234567",
//            ),
            // TODO need to find a host for SigNoz Cloud
//            SignozLogger(host = "")
        ),
    ) {
        appScope.launch {
            PrintTests.run(period = 300.milliseconds)
        }

        ComposeViewport(document.body!!) {
            UILoggerView()
        }
    }
}
