@file:OptIn(ExperimentalForeignApi::class)

package com.cws.print

import kotlinx.cinterop.ExperimentalForeignApi

actual fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit) {
    NSExceptionHandler.install(crashReportFilepath)
    installDarwinExceptionHandler(crashReportFilepath)
}
