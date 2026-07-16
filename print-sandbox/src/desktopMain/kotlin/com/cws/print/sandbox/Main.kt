package com.cws.print.sandbox

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main() {
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    Print.install(
        loggers = setOf(
            ConsoleLogger(),
            FileLogger("logs/print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}.log"),
            UILogger,
            // TODO need to find a host for SigNoz Cloud
//            SignozLogger(host = "")
        ),
    ) {
        application {
            appScope.launch {
                PrintTests.run().join()
            }

            Window(onCloseRequest = ::exitApplication) {
                UILoggerView()
            }
        }
    }
}
