@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

external interface IDBObjectStore : JsAny {
    fun add(value: JsAny): IDBRequest
}