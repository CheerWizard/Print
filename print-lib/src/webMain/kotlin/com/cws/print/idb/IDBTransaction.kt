@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

external interface IDBTransaction : JsAny {
    var onerror: ((JsAny) -> Unit)?
    var onabort: ((JsAny) -> Unit)?
    var oncomplete: ((JsAny) -> Unit)?
    val error: JsAny?
    fun objectStore(name: String): IDBObjectStore
    fun abort()
}
