package com.cws.print

actual fun GlobalExceptionHandler(context: PrintContext, block: (Throwable) -> Unit) {
    NativeExceptionHandler.install(context.getFilepath("PrintCrash.log"))
}
