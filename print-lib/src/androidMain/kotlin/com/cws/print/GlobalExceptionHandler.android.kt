package com.cws.print

actual fun GlobalExceptionHandler(context: PrintContext, block: (Throwable) -> Unit) {
    val previous = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        block(throwable)
        previous?.uncaughtException(thread, throwable)
    }
    NativeExceptionHandler.install(context.getFilepath("PrintCrash.log"))
}