package com.cws.print

expect fun GlobalExceptionHandler(context: PrintContext, block: (Throwable) -> Unit)