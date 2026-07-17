@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

external interface IDBDatabase : JsAny {
    fun transaction(storeName: String, mode: String): IDBTransaction
    fun createObjectStore(name: String, options: IDBObjectStoreParameters): IDBObjectStore
    fun close()
}
