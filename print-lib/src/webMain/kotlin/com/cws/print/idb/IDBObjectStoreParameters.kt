@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

external interface IDBObjectStoreParameters : JsAny {
    var keyPath: String?
    var autoIncrement: Boolean?
}

fun IDBObjectStoreParameters(
    keyPath: String?,
    autoIncrement: Boolean?,
): IDBObjectStoreParameters {
    val value: IDBObjectStoreParameters = js("({})")
    return value.apply {
        this.keyPath = keyPath
        this.autoIncrement = autoIncrement
    }
}
