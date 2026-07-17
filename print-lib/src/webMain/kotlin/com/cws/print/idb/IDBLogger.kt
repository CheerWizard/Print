@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import com.cws.print.WebStorageLogger
import com.cws.print.LogLevel
import com.cws.print.consoleError
import com.cws.print.consoleInfo
import com.cws.print.formatDateTime
import com.cws.print.getCurrentTimeMillis
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toInt
import kotlin.js.unsafeCast
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// IndexedDB storage logger, periodically flushes log entries from list and clears logs from storage if they reach max capacity
class IDBLogger(
    private val name: String,
    private val version: Int = 1,
    private val flushPeriod: Duration = 3.seconds,
    private val maxLogsCount: Int = 1000,
) : WebStorageLogger {

    private var openRequest: IDBOpenDBRequest? = null
    private var indexedDB: IDBDatabase? = null

    private val logs = ArrayList<IDBLogEntry>(64)

    private val flushScope = CoroutineScope(Dispatchers.Default)
    private var flushJob: Job? = null

    private val lock = ReentrantLock()

    override fun open() {
        if (openRequest != null || indexedDB != null) return

        val openRequest = IDB.open(name, version)

        openRequest.onupgradeneeded = {
            openRequest.result?.unsafeCast<IDBDatabase>()?.let { db ->
                val store = db.createObjectStore("logs", IDBObjectStoreParameters(keyPath = "id", autoIncrement = true))
                store.createIndex("timestamp", "timestamp", IDBIndexParameters(unique = false))
            }
        }

        openRequest.onsuccess = {
            indexedDB = openRequest.result?.unsafeCast<IDBDatabase>()
        }

        openRequest.onerror = {
            consoleError("IndexedDB: Error thrown", it)
        }

        openRequest.onblocked = {
            consoleError("IndexedDB: open database blocked by another connection", it)
        }

        flushJob?.cancel()
        flushJob = flushScope.launch {
            while (isActive) {
                delay(flushPeriod)
                flushLogs()
            }
        }
    }

    override fun close() {
        indexedDB?.close()
        flushJob?.cancel()
        flushJob = null
    }

    override fun log(
        logLevel: LogLevel,
        tag: String,
        message: String,
        exception: Throwable?,
    ) {
        lock.withLock {
            val timestamp = getCurrentTimeMillis()
            val dateTime = timestamp.milliseconds.formatDateTime("dd.MM.YYYY HH:mm:ss")
            logs.add(
                IDBLogEntry(
                    timestamp = timestamp.toDouble(),
                    dateTime = dateTime,
                    logLevel = logLevel,
                    tag = tag,
                    message = message,
                    exception = exception,
                )
            )
        }
        // flush immediately to database a FATAL level logs
        // this will also flush all logs before FATAL, which is a correct flow,
        // history of logs will give a better diagnostics for user
        if (logLevel == LogLevel.FATAL) {
            flushLogs()
        }
    }

    private fun flushLogs() {
        if (logs.isEmpty()) return

        indexedDB?.let { db ->
            val transaction = db.transaction("logs", "readwrite")

            transaction.onerror = {
                consoleError("IndexedDB: transaction failed", it)
            }

            transaction.onabort = {
                consoleError("IndexedDB: transaction aborted", it)
            }

            val store = transaction.objectStore("logs")

            lock.withLock {
                logs.forEach {
                    store.put(it)
                }
                logs.clear()
            }

            IDBAwaitRequest(
                tag = "IndexedDB",
                request = store::count,
                onSuccess = {
                    val logsCount = it.toInt()
                    if (logsCount > maxLogsCount) {
                        removeLogsFromStart("IndexedDB", store, logsCount)
                    }
                },
                onError = { exception ->
                    consoleError("IndexedDB: thrown error when getting logs count ${exception.stackTraceToString()}")
                },
            )
        }
    }

    fun removeLogsFromStart(
        tag: String,
        store: IDBObjectStore,
        excess: Int,
    ) {
        var deleted = 0
        val request = store.openCursor() // ascending = oldest first, since keys are autoIncrement ids

        IDBIterateCursor(
            tag = "IndexedDB",
            request = request,
            onEach = { cursor ->
                if (deleted < excess) {
                    cursor.delete()
                    deleted++
                    true // continue unless delete all
                } else {
                    false // stop when deleted enough
                }
            },
            onDone = {
                consoleInfo("Trimmed $deleted old logs")
            },
            onError = {
                consoleError("${tag}: throws error in removeLogsFromStart", it)
            },
        )
    }

}