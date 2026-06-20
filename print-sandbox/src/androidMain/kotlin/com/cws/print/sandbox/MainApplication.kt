package com.cws.print.sandbox

import android.app.Application
import com.cws.print.AndroidPrintContext
import com.cws.print.Print

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Print.install(AndroidPrintContext(applicationContext)) {

        }
    }

}