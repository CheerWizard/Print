package com.cws.print

import platform.windows.CreateDirectoryA

actual fun makeDirs(path: String) {
    val parts = path.split("\\", "/").filter { it.isNotEmpty() }
    var current = ""
    for (part in parts) {
        current += "$part\\"
        CreateDirectoryA(current, null) // ignore result - ERROR_ALREADY_EXISTS is fine
    }
}
