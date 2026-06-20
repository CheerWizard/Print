package com.cws.print

interface PrintContext {
    fun getFilepath(filename: String): String
}