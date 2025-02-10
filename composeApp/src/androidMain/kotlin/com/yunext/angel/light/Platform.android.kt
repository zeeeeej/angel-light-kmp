package com.yunext.angel.light

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String =
        "Android ${Build.VERSION.SDK_INT} ${Build.MANUFACTURER} ${Build.BOARD} ${Build.BRAND}" + " ,versionName:" + BuildConfigX.VERSION +
                " ,versionCode:" + BuildConfigX.VERSION_CODE
}

actual fun getPlatform(): Platform = AndroidPlatform()