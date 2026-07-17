package com.cws.print.sandbox

import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

// exposed to Swift
object MainApplication {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun create() {
        Print.install(
            loggers = setOf(
                ConsoleLogger(),
                FileLogger("logs/print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}.log"),
                UILogger,
            ),
        ) {
            scope.launch {
                PrintTests.run(2.seconds).join()
            }
        }
    }

}
