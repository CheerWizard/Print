package com.cws.print

actual fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        block(throwable)
        previous?.uncaughtException(thread, throwable)
    }
    NativeExceptionHandler.install(crashReportFilepath)
}
