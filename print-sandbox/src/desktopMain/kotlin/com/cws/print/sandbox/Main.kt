package com.cws.print.sandbox

import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.JVMPrintContext
import com.cws.print.Print
import kotlinx.coroutines.runBlocking

fun main() {
    Print.install(
        context = JVMPrintContext(),
        loggers = setOf(
            ConsoleLogger(),
            FileLogger("logs/PrintSandbox.log"),
            // TODO need to find a host for SigNoz Cloud
//            SignozLogger(host = "")
        ),
    ) {
        runBlocking {
            PrintTests.run().join()
        }
    }
}