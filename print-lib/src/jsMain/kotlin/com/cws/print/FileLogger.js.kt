package com.cws.print

actual class FileLogger(
    private val name: String
) : Logger {

    private var indexedDB: dynamic? = null

    private val logs = StringBuilder()

    actual override fun open() {
        indexedDB = js("window.indexedDB.open(name, 1)")

        indexedDB.onupgradeneeded = {
            val db = indexedDB.result
            db.createObjectStore("logs", js("{ keyPath: 'id', autoIncrement: true }"))
        }

        indexedDB.onsuccess = {
            val db = indexedDB.result
            val transaction = db.transaction("logs", "readwrite")
            val store = transaction.objectStore("logs")
            val log = logs.toString()
            logs.clear()
            store.add(js("{ message: log, timestamp: Date.now() }"))
        }

        indexedDB.onerror = {
            console.error("IndexedDB error thrown", indexedDB.error)
        }
    }

    actual override fun close() {
        val db = indexedDB.result
        db.close()
    }

    actual override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        logs.appendLine(formatLog(logLevel, tag, message, exception))
    }

}