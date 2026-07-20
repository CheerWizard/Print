package com.cws.print

actual object JniLibrary {

    actual fun load(libraryName: String) {
        System.loadLibrary(libraryName)
    }

}