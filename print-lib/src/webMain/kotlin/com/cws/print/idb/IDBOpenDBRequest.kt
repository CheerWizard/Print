@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

external interface IDBOpenDBRequest : IDBRequest<JsAny> {
    var onupgradeneeded: ((JsAny) -> Unit)?
    var onblocked: ((JsAny) -> Unit)?
}
