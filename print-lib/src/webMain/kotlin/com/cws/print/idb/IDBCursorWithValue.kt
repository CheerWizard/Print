@file:OptIn(ExperimentalWasmJsInterop::class)

package com.cws.print.idb

import com.cws.print.consoleError
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.definedExternally

external interface IDBCursorWithValue : JsAny {
    val value: JsAny?
    fun delete(): IDBRequest<JsAny>
    fun `continue`(key: JsAny? = definedExternally)
}

fun IDBIterateCursor(
    tag: String,
    request: IDBRequest<JsAny>,
    onEach: (IDBCursorWithValue) -> Boolean, // return true to continue, false to stop
    onDone: () -> Unit,
    onError: (JsAny?) -> Unit,
) {
    request.onsuccess = {
        val cursor = request.result as? IDBCursorWithValue
        if (cursor != null) {
            val keepGoing = onEach(cursor)
            if (keepGoing) {
                cursor.`continue`()
            } else {
                onDone()
            }
        } else {
            onDone() // cursor exhausted
        }
    }
    request.onerror = {
        consoleError("$tag: cursor failed", request.error)
        onError(request.error)
    }
}