package com.cws.print

expect object JniLibrary {
    fun load(libraryName: String)
}

object NativeExceptionHandler {

    init {
        JniLibrary.load("native_exception_handler")
    }

    external fun install(filepath: String)

}