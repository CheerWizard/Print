@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

external interface IDBTransaction : JsAny {
    fun objectStore(name: String): IDBObjectStore
}