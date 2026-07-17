package com.cws.print.sandbox

import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun main() {
    Print.install(
        loggers = setOf(
            ConsoleLogger(),
            FileLogger("logs/print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}.log"),
            // TODO need to find a host for SigNoz Cloud
//            SignozLogger(host = "")
        ),
    ) {
        runBlocking {
            PrintTests.run(period = 1.seconds).join()
        }
    }
}
