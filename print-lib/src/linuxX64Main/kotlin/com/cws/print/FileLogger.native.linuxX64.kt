package com.cws.print

import platform.posix.S_IRWXU
import platform.posix.mkdir

internal actual fun makeDirs(path: String) {
    val parts = path.split("/").filter { it.isNotEmpty() }
    var current = if (path.startsWith("/")) "/" else ""
    for (part in parts) {
        current += "$part/"
        mkdir(current, S_IRWXU.toUInt()) // ignore result - EEXIST is fine
    }
}
