package com.cws.print.sandbox.app

import android.app.Application
import com.cws.print.ConsoleLogger
import com.cws.print.FileLogger
import com.cws.print.Print
import com.cws.print.getCurrentTimestamp
import com.cws.print.sandbox.PrintTests
import com.cws.print.sandbox.UILogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainApplication : Application() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        Print.install(
            loggers = setOf(
                ConsoleLogger(),
                FileLogger(applicationContext, "logs/print-sandbox-${getCurrentTimestamp("dd.MM.YYYY-HH:mm:ss")}.log"),
                UILogger,
            ),
        ) {
            scope.launch {
                PrintTests.run().join()
            }
        }
    }

}
