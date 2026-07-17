@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js
import kotlin.js.unsafeCast

internal fun JsAnyObject(): JsAny = js("({})")

fun <T : JsAny> JsObject(): T = JsAnyObject().unsafeCast<T>()