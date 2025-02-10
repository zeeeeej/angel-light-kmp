package com.yunext.angel.light

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion + " ,versionName:" + BuildConfigX.VERSION + " ," +
                " ,versionCode:" + BuildConfigX.VERSION_CODE
}

actual fun getPlatform(): Platform = IOSPlatform()