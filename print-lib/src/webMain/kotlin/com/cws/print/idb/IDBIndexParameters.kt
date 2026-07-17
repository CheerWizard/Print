@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

external interface IDBIndexParameters : JsAny {
    var unique: Boolean?
    var multiEntry: Boolean?
}

fun IDBIndexParameters(
    unique: Boolean,
    multiEntry: Boolean? = null,
): IDBIndexParameters {
    val value: IDBIndexParameters = js("({})")
    return value.apply {
        this.unique = unique
        this.multiEntry = multiEntry
    }
}
