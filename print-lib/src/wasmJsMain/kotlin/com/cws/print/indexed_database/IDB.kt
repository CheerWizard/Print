@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

fun IDB_openDatabase(name: String): IDBOpenDBRequest =
    js("window.indexedDB.open(name, 1)")

fun IDB_getKeypathOptions(): JsAny =
    js("({ keyPath: 'id', autoIncrement: true })")

fun IDB_log(message: String, timestamp: Double): JsAny =
    js("({ message: message, timestamp: timestamp })")
