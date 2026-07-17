@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny

external interface IDBRequest<T : JsAny> : JsAny {
    var onsuccess: ((T) -> Unit)?
    var onerror: ((JsAny) -> Unit)?
    val result: T?
    val error: JsAny?
}

inline fun <T : JsAny> IDBAwaitRequest(
    tag: String,
    request: () -> IDBRequest<T>,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
): IDBRequest<T> {
    return request().apply {
        onsuccess = {
            onSuccess(it)
        }
        onerror = {
            onError(Throwable("$tag: Request Failed: $it"))
        }
    }
}
