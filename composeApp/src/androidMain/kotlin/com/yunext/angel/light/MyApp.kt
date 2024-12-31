package com.yunext.angel.light

import android.app.Application
import com.yunext.angel.light.di.KoinInit
import org.koin.android.ext.koin.androidContext

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CTX = this
        KoinInit.init { androidContext(this@MyApp) }
    }

    companion object {
        lateinit var CTX: Application
    }
}