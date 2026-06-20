package com.cws.print

import java.io.File

class JVMPrintContext : PrintContext {

    override fun getFilepath(filename: String): String {
        val dir = File("logs")
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, filename).absolutePath
    }

}