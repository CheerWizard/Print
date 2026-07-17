@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

object IDB {
    fun open(name: String, version: Int): IDBOpenDBRequest = js("window.indexedDB.open(name, version)")
}