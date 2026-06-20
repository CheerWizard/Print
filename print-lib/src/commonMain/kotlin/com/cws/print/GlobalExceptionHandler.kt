package com.cws.print

expect fun GlobalExceptionHandler(crashReportFilepath: String, block: (Throwable) -> Unit)