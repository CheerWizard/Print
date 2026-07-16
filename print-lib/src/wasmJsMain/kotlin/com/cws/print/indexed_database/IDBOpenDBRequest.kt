@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

external interface IDBOpenDBRequest : IDBRequest {
    var onupgradeneeded: ((JsAny) -> Unit)?
}