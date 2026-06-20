package com.cws.print

actual fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit) {
    installPosixExceptionHandler(crashReportFilepath)
}