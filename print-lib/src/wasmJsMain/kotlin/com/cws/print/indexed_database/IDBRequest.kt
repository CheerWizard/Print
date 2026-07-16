@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.indexed_database

external interface IDBRequest : JsAny {
    var onsuccess: ((JsAny) -> Unit)?
    var onerror: ((JsAny) -> Unit)?
    val result: JsAny
    val error: JsAny?
}
