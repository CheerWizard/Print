package com.cws.print

expect open class PlatformNativeExceptionHandler() {
    protected fun init()
}

object NativeExceptionHandler : PlatformNativeExceptionHandler() {

    init {
        init()
    }

    external fun install(filepath: String)

}