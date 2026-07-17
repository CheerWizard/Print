@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

internal fun IDB_open(name: String, version: Int): IDBOpenDBRequest =
    js("window.indexedDB.open(name, version)")