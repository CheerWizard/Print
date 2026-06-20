package com.cws.print

class NativePrintContext : PrintContext {

    override fun getFilepath(filename: String): String {
        return "./$filename"
    }

}