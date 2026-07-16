@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

external interface IDBDatabase : JsAny {
    fun createObjectStore(name: String, options: JsAny): JsAny
    fun transaction(storeName: String, mode: String): IDBTransaction
    fun close()
}