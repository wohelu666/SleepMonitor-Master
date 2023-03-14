package com.example.sleepmonitor_master_v3

import android.app.Application
import android.content.Context

class MyApp :Application() {
    companion object{
        lateinit var context :Context
        @JvmStatic
        fun context(): Context {
            return context
        }


    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        WriteLogUtil.init(this)
    }
}