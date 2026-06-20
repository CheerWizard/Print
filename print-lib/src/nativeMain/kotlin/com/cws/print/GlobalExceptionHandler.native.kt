package com.cws.print

actual fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit) {
    NativeExceptionHandler.install(crashReportFilepath)
}