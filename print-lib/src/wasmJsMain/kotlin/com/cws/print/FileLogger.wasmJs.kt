@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print

import com.cws.print.indexed_database.IDBDatabase
import com.cws.print.indexed_database.IDBOpenDBRequest
import com.cws.print.indexed_database.IDB_getKeypathOptions
import com.cws.print.indexed_database.IDB_log
import com.cws.print.indexed_database.IDB_openDatabase
import kotlin.js.unsafeCast

actual class FileLogger(
    private val name: String
) : Logger {

    private val logs = StringBuilder()

    private var openDBRequest: IDBOpenDBRequest? = null

    actual override fun open() {
        openDBRequest = IDB_openDatabase(name)
        openDBRequest?.let { openDBRequest ->
            openDBRequest.onupgradeneeded = {
                val db = openDBRequest.result.unsafeCast<IDBDatabase>()
                db.createObjectStore("logs", IDB_getKeypathOptions())
            }

            openDBRequest.onsuccess = {
                val db = openDBRequest.result.unsafeCast<IDBDatabase>()
                val store = db.transaction("logs", "readwrite").objectStore("logs")
                val log = logs.toString()
                logs.clear()
                store.add(IDB_log(log, getCurrentTimeMillis().toDouble()))
            }

            openDBRequest.onerror = {
                consoleError("Error thrown from IndexedDB ${openDBRequest.error?.toThrowableOrNull()?.stackTraceToString().orEmpty()}")
            }
        }
    }

    actual override fun close() {
        openDBRequest?.result?.unsafeCast<IDBDatabase>()?.close()
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