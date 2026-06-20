package com.cws.print

actual open class PlatformNativeExceptionHandler actual constructor() {

    protected actual fun init() {
        System.loadLibrary("native_exception_handler")
    }

}