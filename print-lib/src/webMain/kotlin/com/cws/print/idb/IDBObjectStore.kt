@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsNumber
import kotlin.js.definedExternally

external interface IDBObjectStore : JsAny {
    fun put(value: IDBLogEntry): IDBRequest<JsAny>
    fun createIndex(name: String, keyPath: String, options: IDBIndexParameters): JsAny
    fun index(name: String): JsAny
    fun count(): IDBRequest<JsNumber>
    fun openCursor(range: JsAny = definedExternally): IDBRequest<JsAny>
}