package com.cws.print

import android.content.Context
import java.io.File

class AndroidPrintContext(private val context: Context) : PrintContext {

    override fun getFilepath(filename: String): String {
        return File(context.filesDir, filename).absolutePath
    }

}