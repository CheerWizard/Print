package com.cws.print.com.cws.print.sandbox

import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import com.cws.print.sandbox.PrintTests
import kotlinx.coroutines.runBlocking

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
            PrintTests.run().join()
        }
    }
}
