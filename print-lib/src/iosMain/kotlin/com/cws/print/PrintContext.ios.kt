package com.cws.print

import platform.Foundation.NSHomeDirectory

class IOSPrintContext : PrintContext {

    override fun getFilepath(filename: String): String {
        return NSHomeDirectory() + "/Library/Caches/$filename"
    }

}