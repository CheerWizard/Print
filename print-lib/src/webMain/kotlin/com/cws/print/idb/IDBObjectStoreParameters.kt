@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import com.cws.print.JsObject
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
    val value: IDBObjectStoreParameters = JsObject()
    return value.apply {
        this.keyPath = keyPath
        this.autoIncrement = autoIncrement
    }
}
